package plugins.Spider.web;

import freenet.client.HighLevelSimpleClient;
import freenet.node.NodeClientCore;
import plugins.Spider.Spider;

public class MainPageToadlet extends WebPageToadlet<MainPage> {

	private final Spider spider;

	protected MainPageToadlet(HighLevelSimpleClient client, Spider spider, NodeClientCore core) {
		super(client, core, "/spider/");
		this.spider = spider;
	}

	@Override
	protected MainPage getPage() {
		return new MainPage(spider);
	}

}
