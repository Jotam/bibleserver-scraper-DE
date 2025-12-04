package org.zephyrsoft.bibleserverscraper;

import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zephyrsoft.bibleserverscraper.model.Book;
import org.zephyrsoft.bibleserverscraper.model.BookChapter;
import org.zephyrsoft.bibleserverscraper.model.ChapterScrapeResult;
import org.zephyrsoft.bibleserverscraper.model.Translation;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scraper {
	private static final Logger LOG = LoggerFactory.getLogger(Scraper.class);

	private Random random = new Random();

	public void scrape(String directory) {
		try (WebClient client = new WebClient()) {
			client.getOptions().setCssEnabled(false);
			client.getOptions().setJavaScriptEnabled(false);
			client.getOptions().setHistoryPageCacheLimit(1);

			AtomicBoolean allScrapedSuccessfully = new AtomicBoolean(true);
			do {
				Translation.forEach(translation -> Book.books()
					.flatMap(book -> book.bookChapters())
					.forEach(bookChapter -> {
						ChapterScrapeResult chapterScrapeResult = scrapeChapter(directory, client, translation, bookChapter);
						if (!chapterScrapeResult.wasSuccessful()) {
							allScrapedSuccessfully.set(false);
						}
						if (chapterScrapeResult.shouldWait()) {
							sleepRandomTime();
						}
					}));
			} while (!allScrapedSuccessfully.get());
		}
	}

	private ChapterScrapeResult scrapeChapter(String directory, WebClient client, Translation translation, BookChapter bookChapter) {
		String translationAbbreviation = translation.getAbbreviation();
		String bookChapterName = translation.nameOf(bookChapter);

		File targetFile = new File(directory + File.separator + translationAbbreviation + "-" +
			bookChapter.getBook().getOrdinal() + "-" + bookChapter.getNameGerman() + ".txt"); // files always named in German
		if (targetFile.exists()) {
			LOG.debug("not fetching {} in {}, file {} exists", bookChapterName, translationAbbreviation, targetFile);
			return new ChapterScrapeResult(false, true);
		} else {
			try {
				LOG.debug("fetching {} in {}", bookChapterName, translationAbbreviation);
				String searchUrl = "https://www.bibleserver.com/text/" + URLEncoder.encode(translationAbbreviation, "UTF-8")
					+ "/" + URLEncoder.encode(bookChapterName, "UTF-8");
				Page page = client.getPage(searchUrl);

				if (page.isHtmlPage()) {
					handleChapter(targetFile, (HtmlPage)page);
					return new ChapterScrapeResult(true, true);
				} else {
					LOG.warn("result was not HTML: {}", page.getWebResponse().getContentAsString(StandardCharsets.UTF_8));
					return new ChapterScrapeResult(true, false);
				}
			} catch (Exception e) {
				LOG.warn("error fetching " + bookChapterName + " in " + translationAbbreviation, e);
				return new ChapterScrapeResult(true, false);
			}
		}
	}

	private void handleChapter(File targetFile, HtmlPage page) throws IOException {
		List<DomNode> verses = page.<DomNode>getByXPath("//*[contains(@class,'chapter')]//*[contains(@class,'verse-content--hover')]");
		if (verses.isEmpty()) {
			throw new IllegalStateException("no verses found");
		} else {
			List<String> versesText = new LinkedList<>();
			for (DomNode verse : verses) {
				String verseString = verse.<DomNode>getByXPath("./text()").stream()
					.map(node -> node.asText())
					.collect(joining(" "))
					.replaceAll(" {2,}", " ")
					.replaceAll("(\\w) ([\\.!\\?,;:])", "$1$2");
				versesText.add(verseString);
			}
			Files.write(targetFile.toPath(), versesText, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		}
	}

	private void sleepRandomTime() {
		try {
			//int seconds = random.nextInt(2) + 1;
			int seconds = 1;
			LOG.debug("waiting for {} seconds", seconds);
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

}
