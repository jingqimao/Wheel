package com.chs.wheel.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class TXTUtils {

	/**
	 * 
	 * <p>cleanBasic</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-05-30 16:31
	 *
	 * @param bodyHtml
	 * @return
	 */
	public static String cleanBasic(String bodyHtml) {
		return Jsoup.clean(bodyHtml,"", Whitelist.basic(),new Document.OutputSettings().prettyPrint(false));
	}
	
	/**
	 * 
	 * <p>cleanSimpleText</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-05-30 16:32
	 *
	 * @param bodyHtml
	 * @return
	 */
	public static String cleanSimpleText(String bodyHtml) {
		return Jsoup.clean(bodyHtml, Whitelist.simpleText());
	}
}
