package org.zephyrsoft.bibleserverscraper;

import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zephyrsoft.bibleserverscraper.model.Book;
import org.zephyrsoft.bibleserverscraper.model.Translation;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scraper {
	private static final Logger LOG = LoggerFactory.getLogger(Scraper.class);

	private Random random = new Random();

	public static void main(String args[]) {
		if (args.length < 1 || directoryNotCorrect(args[0])) {
			LOG.error("please provide an existing target directory as first parameter");
		} else {
			Scraper scraper = new Scraper();
			scraper.scrape(args[0]);
		}
	}

	private static boolean directoryNotCorrect(String dir) {
		File directory = new File(dir);
		return !directory.exists() || !directory.isDirectory() || !directory.canWrite();
	}

	public void scrape(String directory) {
		try (WebClient client = new WebClient()) {
			client.getOptions().setCssEnabled(false);
			client.getOptions().setJavaScriptEnabled(false);
			client.getOptions().setHistoryPageCacheLimit(1);

			Translation.abbreviations().forEach(translation -> {
				Book.books().flatMap(book -> book.bookChapters()).forEach(bookChapter -> {
					boolean shouldWait = scrapeChapter(directory, client, translation, bookChapter);
					if (shouldWait) {
						sleepRandomTime();
					}
				});
			});
		}
	}

	private boolean scrapeChapter(String directory, WebClient client, String translation, String bookChapter) {
		File targetFile = new File(directory + File.separator + translation + "-" + bookChapter + ".txt");
		if (targetFile.exists()) {
			LOG.debug("not fetching {} in {}, file {} exists", bookChapter, translation, targetFile);
			return false;
		} else {
			try {
				String searchUrl = "https://www.bibleserver.com/text/" + translation + "/" + URLEncoder.encode(bookChapter, "UTF-8");
				HtmlPage page = client.getPage(searchUrl);

				handleChapter(targetFile, translation, bookChapter, page);
			} catch (Exception e) {
				LOG.warn("error fetching " + bookChapter + " in " + translation, e);
			}
			return true;
		}
	}

	private void handleChapter(File targetFile, String translation, String bookChapter, HtmlPage page) throws IOException {
		List<DomNode> verses = page.<DomNode>getByXPath("//*[@class='chapter']/*[contains(@class,'verse')]");

		LOG.debug("============= {} / {}", translation, bookChapter);
		List<String> versesText = new LinkedList<>();
		for (DomNode verse : verses) {
			String verseString = verse.<DomNode>getByXPath("./text()").stream()
				.map(node -> node.asText())
				.collect(joining(" "))
				.replaceAll(" {2,}", " ")
				.replaceAll("(\\w) ([\\.!\\?,;:])", "$1$2");
			versesText.add(verseString);
		}
		Files.write(targetFile.toPath(), versesText, StandardOpenOption.CREATE_NEW);
	}

	private void sleepRandomTime() {
		try {
			int seconds = random.nextInt(5) + 1;
			LOG.debug("waiting for {} seconds", seconds);
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

}
