package plugins.Spider.web;

import freenet.client.HighLevelSimpleClient;
import freenet.node.NodeClientCore;
import plugins.Spider.Spider;

public class ConfigPageToadlet extends WebPageToadlet<ConfigPage> {

	private final Spider spider;

	protected ConfigPageToadlet(HighLevelSimpleClient client, Spider spider, NodeClientCore core) {
		super(client, core, "/spider/config");
		this.spider = spider;
	}

	@Override
	protected ConfigPage getPage() {
		return new ConfigPage(spider);
	}

}
