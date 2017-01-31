import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

public class Indexing {
	int collectionword = 0;
	// To store tokens and their occurrences in list of documents
	static HashMap<String, ArrayList<String>> tokenmap = new HashMap<>();
	// HashMap to store token as key and value as arraylist which contain
	// document and frequency of each token in a document as hashmap
	static HashMap<String, HashMap<String, Integer>> tokencount = new HashMap<>();
	public HashMap<String, HashMap<String, Integer>> doccount = new HashMap<>();
	private static String String;
	File[] listOfFiles;

	public HashMap<String, HashMap<String, Integer>> tokenizer() throws IOException 
	{
		String s;
		String[] token = null;
		Porter1 portstem = new Porter1();
		String stem;
		// Retrieving all files from folder
		File folder = new File("C:/JavaPrograms/weblinks");
		listOfFiles = folder.listFiles();
		HashMap<String, Integer> stopwords = readstopwords();
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			HashMap<String,Integer> tokensfreq=new HashMap<String, Integer>();
			Double max = 0.0;
			// Reading contents of each file
			FileReader reader = new FileReader(listOfFiles[i]);
			BufferedReader br = new BufferedReader(reader);
			while ((s = br.readLine()) != null) 
			{
				// Tokenizing on whitespace and removing all punctuation marks,
				// html tags
				// and replace multiple whitespace with single whitespace
				token = s.replaceAll("\\<.*?>", " ").replaceAll("\\W", " ").replaceAll("\\s+", " ").split("\\s");
				for (int j = 0; j < token.length; j++) 
				{
					if (token[j].length() > 0) 
					{
						// Eliminating stop words before stemming
						if (stopwords.containsKey(token[j]))
							continue;
						stem = portstem.stripAffixes(token[j]);
						// Eliminating stop words after stemming
						if (stopwords.containsKey(stem))
							continue;
						tokensfreq.put(stem, 1);
						if(tokensfreq.containsKey(stem))
						{
							Integer count1=tokensfreq.get(stem);
							if (count1 == null)
								count1 = 0;
							count1++;
							tokensfreq.put(stem, count1);
						}
						if (tokenmap.containsKey(stem)) 
						{
							ArrayList<String> listOfDocs = tokenmap.get(stem);
							listOfDocs.add(listOfFiles[i].getName());
						} 
						else 
						{
							ArrayList<String> newdoc = new ArrayList<String>();
							newdoc.add(listOfFiles[i].getName());
							tokenmap.put(stem, newdoc);
						}
						if (tokencount.containsKey(stem))
						{
							HashMap<String, Integer> tokdocfreq = tokencount.get(stem);
							Integer count = tokdocfreq.get(listOfFiles[i].getName());
							if (count == null)
								count = 0;
							count++;
							if (max < count)
								max = count.doubleValue();
							tokdocfreq.put(listOfFiles[i].getName(), count);
						} 
						else
						{
							HashMap<String, Integer> tokdocfreq = new HashMap<String, Integer>();
							tokdocfreq.put(listOfFiles[i].getName(), 1);
							tokencount.put(stem, tokdocfreq);
						}
						
					}
				}
			}
			br.close();	
			doccount.put(listOfFiles[i].getName(), tokensfreq);
		}
		//System.out.println("document tokens:"+doccount);
		//System.out.println(tokencount);
		System.out.println("exit from indexing code");
		return tokencount;
	}
// Function to store stopwords as keys and their frequencies as values into Hashmap
	public HashMap<String, Integer> readstopwords() throws IOException 
	{
		String[] sw;
		String s1;
		HashMap<String, Integer> stopwords = new HashMap<String, Integer>();
		File file1 = new File("D:/Spring-2016/IRS/Assignment1/stopwords.txt");
		FileReader reader1 = new FileReader(file1);
		BufferedReader br1 = new BufferedReader(reader1);
		while ((s1 = br1.readLine()) != null)
		{
			sw = s1.split("\\s");
			for (String word : sw) 
			{
				Integer count = stopwords.get(word);
				stopwords.put(word, (count == null) ? 1 : count + 1);
			}

		}
		br1.close();
		return stopwords;
	}
// Tokenizing the queries and calculating their weight
	public static void main(String[] args) 
	{
		Indexing p1 = new Indexing();	
		
		
	}
}
class NewString1 
{
	public String str;
	NewString1()
	{
		str = "";
	}
}

/**
 * The Porter stemmer for reducing words to their base stem form.
 *
 * @author Fotis Lazarinis
 */

class Porter1 
{
	private String Clean(String str)
	{
		int last = str.length();
		String temp = "";
		Character ch = new Character(str.charAt(0));
		for (int i = 0; i < last; i++) 
		{
			if (ch.isLetterOrDigit(str.charAt(i)))
				temp += str.charAt(i);
		}
		return temp;
	} // clean
	private boolean hasSuffix(String word, String suffix, NewString1 stem)
	{
		String tmp = "";
		if (word.length() <= suffix.length())
			return false;
		if (suffix.length() > 1)
			if (word.charAt(word.length() - 2) != suffix.charAt(suffix.length() - 2))
				return false;
		stem.str = "";
		for (int i = 0; i < word.length() - suffix.length(); i++)
			stem.str += word.charAt(i);
		tmp = stem.str;
		for (int i = 0; i < suffix.length(); i++)
			tmp += suffix.charAt(i);
		if (tmp.compareTo(word) == 0)
			return true;
		else
			return false;
	}
	private boolean vowel(char ch, char prev) 
	{
		switch (ch) 
		{
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			return true;
		case 'y': {

			switch (prev) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return false;

			default:
				return true;
			}
		}

		default:
			return false;
		}
	}

	private int measure(String stem) {

		int i = 0, count = 0;
		int length = stem.length();

		while (i < length) {
			for (; i < length; i++) {
				if (i > 0) {
					if (vowel(stem.charAt(i), stem.charAt(i - 1)))
						break;
				} else {
					if (vowel(stem.charAt(i), 'a'))
						break;
				}
			}

			for (i++; i < length; i++) {
				if (i > 0) {
					if (!vowel(stem.charAt(i), stem.charAt(i - 1)))
						break;
				} else {
					if (!vowel(stem.charAt(i), '?'))
						break;
				}
			}
			if (i < length) {
				count++;
				i++;
			}
		} // while

		return (count);
	}

	private boolean containsVowel(String word) {

		for (int i = 0; i < word.length(); i++)
			if (i > 0) {
				if (vowel(word.charAt(i), word.charAt(i - 1)))
					return true;
			} else {
				if (vowel(word.charAt(0), 'a'))
					return true;
			}

		return false;
	}

	private boolean cvc(String str) {
		int length = str.length();

		if (length < 3)
			return false;

		if ((!vowel(str.charAt(length - 1), str.charAt(length - 2))) && (str.charAt(length - 1) != 'w')
				&& (str.charAt(length - 1) != 'x') && (str.charAt(length - 1) != 'y')
				&& (vowel(str.charAt(length - 2), str.charAt(length - 3)))) {

			if (length == 3) {
				if (!vowel(str.charAt(0), '?'))
					return true;
				else
					return false;
			} else {
				if (!vowel(str.charAt(length - 3), str.charAt(length - 4)))
					return true;
				else
					return false;
			}
		}

		return false;
	}

	private String step1(String str) {

		NewString1 stem = new NewString1();

		if (str.charAt(str.length() - 1) == 's') {
			if ((hasSuffix(str, "sses", stem)) || (hasSuffix(str, "ies", stem))) {
				String tmp = "";
				for (int i = 0; i < str.length() - 2; i++)
					tmp += str.charAt(i);
				str = tmp;
			} else {
				if ((str.length() == 1) && (str.charAt(str.length() - 1) == 's')) {
					str = "";
					return str;
				}
				if (str.charAt(str.length() - 2) != 's') {
					String tmp = "";
					for (int i = 0; i < str.length() - 1; i++)
						tmp += str.charAt(i);
					str = tmp;
				}
			}
		}

		if (hasSuffix(str, "eed", stem)) {
			if (measure(stem.str) > 0) {
				String tmp = "";
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp;
			}
		} else {
			if ((hasSuffix(str, "ed", stem)) || (hasSuffix(str, "ing", stem))) {
				if (containsVowel(stem.str)) {

					String tmp = "";
					for (int i = 0; i < stem.str.length(); i++)
						tmp += str.charAt(i);
					str = tmp;
					if (str.length() == 1)
						return str;

					if ((hasSuffix(str, "at", stem)) || (hasSuffix(str, "bl", stem)) || (hasSuffix(str, "iz", stem))) {
						str += "e";

					} else {
						int length = str.length();
						if ((str.charAt(length - 1) == str.charAt(length - 2)) && (str.charAt(length - 1) != 'l')
								&& (str.charAt(length - 1) != 's') && (str.charAt(length - 1) != 'z')) {

							tmp = "";
							for (int i = 0; i < str.length() - 1; i++)
								tmp += str.charAt(i);
							str = tmp;
						} else if (measure(str) == 1) {
							if (cvc(str))
								str += "e";
						}
					}
				}
			}
		}

		if (hasSuffix(str, "y", stem))
			if (containsVowel(stem.str)) {
				String tmp = "";
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp + "i";
			}
		return str;
	}

	private String step2(String str) {

		String[][] suffixes = { { "ational", "ate" }, { "tional", "tion" }, { "enci", "ence" }, { "anci", "ance" },
				{ "izer", "ize" }, { "iser", "ize" }, { "abli", "able" }, { "alli", "al" }, { "entli", "ent" },
				{ "eli", "e" }, { "ousli", "ous" }, { "ization", "ize" }, { "isation", "ize" }, { "ation", "ate" },
				{ "ator", "ate" }, { "alism", "al" }, { "iveness", "ive" }, { "fulness", "ful" }, { "ousness", "ous" },
				{ "aliti", "al" }, { "iviti", "ive" }, { "biliti", "ble" } };
		NewString1 stem = new NewString1();

		for (int index = 0; index < suffixes.length; index++) {
			if (hasSuffix(str, suffixes[index][0], stem)) {
				if (measure(stem.str) > 0) {
					str = stem.str + suffixes[index][1];
					return str;
				}
			}
		}

		return str;
	}

	private String step3(String str) {

		String[][] suffixes = { { "icate", "ic" }, { "ative", "" }, { "alize", "al" }, { "alise", "al" },
				{ "iciti", "ic" }, { "ical", "ic" }, { "ful", "" }, { "ness", "" } };
		NewString1 stem = new NewString1();

		for (int index = 0; index < suffixes.length; index++) {
			if (hasSuffix(str, suffixes[index][0], stem))
				if (measure(stem.str) > 0) {
					str = stem.str + suffixes[index][1];
					return str;
				}
		}
		return str;
	}

	private String step4(String str) {

		String[] suffixes = { "al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "sion",
				"tion", "ou", "ism", "ate", "iti", "ous", "ive", "ize", "ise" };

		NewString1 stem = new NewString1();

		for (int index = 0; index < suffixes.length; index++) {
			if (hasSuffix(str, suffixes[index], stem)) {

				if (measure(stem.str) > 1) {
					str = stem.str;
					return str;
				}
			}
		}
		return str;
	}

	private String step5(String str) {

		if (str.charAt(str.length() - 1) == 'e') {
			if (measure(
					str) > 1) {/*
								 * measure(str)==measure(stem) if ends in vowel
								 */
				String tmp = "";
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp;
			} else if (measure(str) == 1) {
				String stem = "";
				for (int i = 0; i < str.length() - 1; i++)
					stem += str.charAt(i);

				if (!cvc(stem))
					str = stem;
			}
		}

		if (str.length() == 1)
			return str;
		if ((str.charAt(str.length() - 1) == 'l') && (str.charAt(str.length() - 2) == 'l') && (measure(str) > 1))
			if (measure(
					str) > 1) {/*
								 * measure(str)==measure(stem) if ends in vowel
								 */
				String tmp = "";
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp;
			}
		return str;
	}

	private String stripPrefixes(String str) {

		String[] prefixes = { "kilo", "micro", "milli", "intra", "ultra", "mega", "nano", "pico", "pseudo" };

		int last = prefixes.length;
		for (int i = 0; i < last; i++) {
			if (str.startsWith(prefixes[i])) {
				String temp = "";
				for (int j = 0; j < str.length() - prefixes[i].length(); j++)
					temp += str.charAt(j + prefixes[i].length());
				return temp;
			}
		}

		return str;
	}

	private String stripSuffixes(String str) {

		str = step1(str);
		if (str.length() >= 1)
			str = step2(str);
		if (str.length() >= 1)
			str = step3(str);
		if (str.length() >= 1)
			str = step4(str);
		if (str.length() >= 1)
			str = step5(str);

		return str;
	}

	/**
	 * Takes a String as input and returns its stem as a String.
	 */
	public String stripAffixes(String str) {

		str = str.toLowerCase();
		str = Clean(str);

		if ((str != "") && (str.length() > 2)) {
			str = stripPrefixes(str);

			if (str != "")
				str = stripSuffixes(str);

		}

		return str;
	} // stripAffixes
}