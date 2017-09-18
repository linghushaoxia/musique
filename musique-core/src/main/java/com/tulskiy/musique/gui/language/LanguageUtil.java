package com.tulskiy.musique.gui.language;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**功能说明：语言工具类
 * @author:linghushaoxia
 * @time:2017年9月16日下午12:29:49
 * @version:1.0
 * 为中国羸弱的技术撑起一片自立自强的天空
 */
public class LanguageUtil {
	//本地化语言map,key是标识,value对应的本地化语言文字
	private static Map<String, String> localMap = new ConcurrentHashMap<String, String>();
	//本地化语言资源文件基路径
	private static String languagePathBase = "/com/tulskiy/musique/resources/";
	//初始化
	private static boolean init=false;
	private static void init(){
		if (init) {
			return;
		}
		/**
		 * 加载属性文件,并初始化localMap
		 */
		Properties properties= loadProperties();
		if (properties!=null&&!properties.isEmpty()) {
			Set<Object> keySet = properties.keySet();
			for(Object key:keySet){
				localMap.put((String) key, properties.getProperty((String) key));
			}
		}
		init =true;
	}
	/**
	 * 
	 * 功能说明:获取本地化语言
	 * @param code
	 * @return String
	 * @time:2017年9月16日下午12:35:50
	 * @author:linghushaoxia
	 * @exception:
	 *
	 */
	public static String getLocalText(String code){
		if (!init) {
			init();
		}
		return localMap.get(code);
	}
	/**
	 * 
	 * 功能说明:加载本地化语言路径
	 * @return Properties
	 * @time:2017年9月16日下午12:38:00
	 * @author:linghushaoxia
	 * @exception:
	 *
	 */
	private static Properties loadProperties(){
		Properties properties = new Properties();
		try {
			String language = System.getenv("musique-lang");
			if (language==null||"".equals(language.trim())) {
				language="zh-CN";
			}
			InputStream inputStream = LanguageUtil.class.getResourceAsStream(buildPath(language));
			properties.load(new InputStreamReader(inputStream,LanguageConfigconst.CHAR_SET));
		} catch (FileNotFoundException e) {
			System.out.println("load language resources has error");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("load language resources has error");
			e.printStackTrace();
		}
		return properties;
	}
	private static String buildPath(String loacal){
		String path = languagePathBase+"language_zh-CN.properties";
		if (loacal==null||"".equals(loacal.trim())) {
			return path;
		}
		//
		path=languagePathBase+"language_"+loacal+".properties";
		return path;
	}
	
}

/**
* 现实就是实现理想的过程
*/