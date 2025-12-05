package org.zephyrsoft.bibleserverscraper.model;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public enum Book {

	_01("1.Mose", "Genesis", "Génesis", 50),
	_02("2.Mose", "Exodus", "Éxodo", 40),
	_03("3.Mose", "Leviticus", "Levitico", 27),
	_04("4.Mose", "Numbers", "Números", 36),
	_05("5.Mose", "Deuteronomy", "Deuteronomio", 34),
	_06("Josua", "Joshua", "Josué", 24),
	_07("Richter", "Judges", "Jueces", 21),
	_08("Rut", "Ruth", "Rut", 4),
	_09("1.Samuel", "1Samuel", "1Samuel", 31),
	_10("2.Samuel", "2Samuel", "2Samuel", 24),
	_11("1.Könige", "1Kings", "1Reyes", 22),
	_12("2.Könige", "2Kings", "2Reyes", 25),
	_13("1.Chronik", "1Chronicles", "1Crónicas", 29),
	_14("2.Chronik", "2Chronicles", "2Crónicas", 36),
	_15("Esra", "Ezra", "Esdras", 10),
	_16("Nehemia", "Nehemiah", "Nehemías", 13),
	_17("Esther", "Esther", "Ester", 10),
	_18("Hiob", "Job", "Job", 42),
	_19("Psalmen", "Psalms", "Salmos", 150),
	_20("Sprüche", "Proverbs", "Proverbios", 31),
	_21("Prediger", "Ecclesiastes", "Eclesiastés", 12),
	_22("Hoheslied", "Song of Solomon", "Cantares", 8),
	_23("Jesaja", "Isaiah", "Isaías", 66),
	_24("Jeremia", "Jeremiah", "Jeremías", 52),
	_25("Klagelieder", "Lamentations", "Lamentaciones", 5),
	_26("Hesekiel", "Ezekiel", "Ezequiel", 48),
	_27("Daniel", "Daniel", "Daniel", 14),
	_28("Hosea", "Hosea", "Oseas", 14),
	_29("Joel", "Joel", "Joel", 4),
	_30("Amos", "Amos", "Amós", 9),
	_31("Obadja", "Obadiah", "Abdías", 1),
	_32("Jona", "Jonah", "Jonás", 4),
	_33("Micha", "Micah", "Miqueas", 7),
	_34("Nahum", "Nahum", "Nahum", 3),
	_35("Habakuk", "Habbakuk", "Habacuc", 3),
	_36("Zefanja", "Zephaniah", "Sofonías", 3),
	_37("Haggai", "Haggai", "Hageo", 2),
	_38("Sacharja", "Zechariah", "Zacarías", 14),
	_39("Maleachi", "Malachi", "Malaquías", 3),
	_40("Matthäus", "Matthew", "Mateo", 28),
	_41("Markus", "Mark", "Marcos", 16),
	_42("Lukas", "Luke", "Lucas", 24),
	_43("Johannes", "John", "Juan", 21),
	_44("Apostelgeschichte", "Acts", "Hechos", 28),
	_45("Römer", "Romans", "Romanos", 16),
	_46("1.Korinther", "1Corinthians", "1Corintios", 16),
	_47("2.Korinther", "2Corinthians", "2Corintios", 13),
	_48("Galater", "Galatians", "Gálatas", 6),
	_49("Epheser", "Ephesians", "Efesios", 6),
	_50("Philipper", "Philippians", "Filipenses", 4),
	_51("Kolosser", "Colossians", "Colosenses", 4),
	_52("1.Thessalonicher", "1Thessalonians", "1Tesalonicenses", 5),
	_53("2.Thessalonicher", "2Thessalonians", "2Tesalonicenses", 3),
	_54("1.Timotheus", "1Timothy", "1Timoteo", 6),
	_55("2.Timotheus", "2Timothy", "2Timoteo", 4),
	_56("Titus", "Titus", "Tito", 3),
	_57("Philemon", "Philemon", "Filemón", 1),
	_58("Hebräer", "Hebrews", "Hebreos", 13),
	_59("Jakobus", "James", "Santiago", 5),
	_60("1.Petrus", "1Peter", "1Pedro", 5),
	_61("2.Petrus", "2Peter", "2Pedro", 3),
	_62("1.Johannes", "1John", "1Juan", 5),
	_63("2.Johannes", "2John", "2Juan", 1),
	_64("3.Johannes", "3John", "3Juan", 1),
	_65("Judas", "Jude", "Judas", 1),
	_66("Offenbarung", "Revelation", "Apocalipsis", 22);

	public static Stream<Book> books() {
		return Stream.of(values());
	}

	private String nameGerman;
	private String nameEnglish;
	private String nameSpanish;
	private int chapters;

	private Book(String nameGerman, String nameEnglish, String nameSpanish, int chapters) {
		this.nameGerman = nameGerman;
		this.nameEnglish = nameEnglish;
		this.nameSpanish = nameSpanish;
		this.chapters = chapters;
	}

	public String getNameGerman() {
		return nameGerman;
	}

	public String getPrintNameGerman() {
		return getNameGerman()
			.replaceAll("^([1-5]\\.)([A-Z])", "$1 $2");
	}

	public String getOrdinal() {
		return name().replace("_", "");
	}

	public int getOrdinalNumber() {
		return Integer.parseInt(getOrdinal());
	}

	public Stream<BookChapter> bookChapters() {
		if (chapters == 1) {
			return Stream.of(new BookChapter(this, 1, nameGerman, nameEnglish, nameSpanish));
		} else {
			return IntStream.range(1, chapters + 1)
				.mapToObj(chapter -> new BookChapter(this, chapter, nameGerman + chapter, nameEnglish + chapter, nameSpanish + chapter));
		}
	}

}
