package it.therickys93.wiki;

/**
 * Created by Ricky on 10/13/18.
 */

public class Wiki {
    public class QRCode {
        public static final String SETTINGS = "SETTINGS";
        public static final String URL      = "URL";
    }
    public class AI {
        public static final String DEFAULT_URL = "http://server.wiki.home/v1/wiki";
        public class Settings {
            public static final String NAME   = "MySettingsWikiAI";
            public static final String SERVER = "WIKISERVER_URL";
        }
    }
    public class Controller {
        public static final String DEFAULT_URL      = "http://controller.wiki.home";
        public static final String DEFAULT_FILENAME = "wiki.json";
        public class Settings {
            public static final String NAME   = "MySettingsWiki";
            public static final String SERVER = "WIKI_SERVER";
        }
        public class Response {
            public static final String OK    = "OK";
            public static final String ERROR = "ERRORE";
            public static final String ON    = "ACCESO";
            public static final String OFF   = "SPENTO";
        }
    }
}
