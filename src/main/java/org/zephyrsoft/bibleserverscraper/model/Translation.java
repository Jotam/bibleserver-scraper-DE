package org.zephyrsoft.bibleserverscraper.model;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public enum Translation {

	NEÜ("NEÜ", "Neue evangelistische Übersetzung", BookChapter::getNameGerman),
	LUT("LUT", "Luther 2017", BookChapter::getNameGerman),
	ELB("ELB", "Elberfelder", BookChapter::getNameGerman),
	HFA("HFA", "Hoffnung für alle", BookChapter::getNameGerman),
	SLT("SLT", "Schlachter 2000", BookChapter::getNameGerman),
	ZB("ZB", "Zürcher Bibel", BookChapter::getNameGerman),
	GNB("GNB", "Gute Nachricht Bibel", BookChapter::getNameGerman),
	EU("EU", "Einheitsübersetzung", BookChapter::getNameGerman),
	MENG("MENG", "Menge Bibel", BookChapter::getNameGerman),
	NLB("NLB", "Neues Leben. Die Bibel", BookChapter::getNameGerman),
	VXB("VXB", "Volxbibel", BookChapter::getNameGerman);
	// ESV("ESV", "English Standard Version", BookChapter::getNameEnglish),
	// NBV("NBV", "Nueva Biblia Viva", BookChapter::getNameSpanish);

	private String abbreviation;
	private String name;
	private Function<BookChapter, String> nameGetter;

	private Translation(String abbreviation, String name, Function<BookChapter, String> nameGetter) {
		this.abbreviation = abbreviation;
		this.name = name;
		this.nameGetter = nameGetter;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public String getName() {
		return name;
	}

	public String nameOf(BookChapter bookChapter) {
		return nameGetter.apply(bookChapter);
	}

	public String fileNameOf(BookChapter bookChapter) {
		return abbreviation + "-" + bookChapter.getBook().getOrdinal() + "-" + bookChapter.getNameGerman() + ".txt";
	}

	public static Stream<Translation> stream() {
		return Stream.of(values());
	}

	public static void forEach(Consumer<Translation> action) {
		stream().forEach(action);
	}
}
