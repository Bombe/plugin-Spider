package plugins.Spider.web;

import java.io.IOException;
import java.net.URI;

import freenet.client.HighLevelSimpleClient;
import freenet.clients.http.PageNode;
import freenet.clients.http.Toadlet;
import freenet.clients.http.ToadletContext;
import freenet.clients.http.ToadletContextClosedException;
import freenet.node.NodeClientCore;
import freenet.support.HTMLNode;
import freenet.support.MultiValueTable;
import freenet.support.api.HTTPRequest;
import plugins.Spider.Spider;

/**
 * Wrapper around a {@link WebPage} to make it usable as a {@link Toadlet}.
 *
 * @param <P> The type of {@link WebPage} being rendered
 */
public abstract class WebPageToadlet<P extends WebPage> extends Toadlet {

	private final NodeClientCore core;
	private final String path;

	public WebPageToadlet(HighLevelSimpleClient client, NodeClientCore core, String path) {
		super(client);
		this.core = core;
		this.path = path;
	}

	/**
	 * The actual page being rendered.
	 */
	protected abstract P getPage();

	@Override
	public String path() {
		return path;
	}

	@Override
	public void handleMethodGET(URI uri, HTTPRequest httpRequest, ToadletContext toadletContext) throws ToadletContextClosedException, IOException {
		runWithContextClassLoader(Spider.class.getClassLoader(), () -> {
			PageNode p = toadletContext.getPageMaker().getPageNode(Spider.pluginName, toadletContext);
			HTMLNode pageNode = p.outer;
			HTMLNode contentNode = p.content;

			P page = getPage();
			page.writeContent(httpRequest, contentNode);

			writeHTMLReply(toadletContext, 200, "OK", null, pageNode.generate());
		});
	}

	public void handleMethodPOST(URI uri, HTTPRequest request, final ToadletContext ctx) throws ToadletContextClosedException, IOException {
		runWithContextClassLoader(Spider.class.getClassLoader(), () -> {
			String formPassword = request.getPartAsString("formPassword", 32);
			if ((formPassword == null) || !formPassword.equals(core.formPassword)) {
				MultiValueTable<String, String> headers = new MultiValueTable<String, String>();
				headers.put("Location", "/spider/config");
				ctx.sendReplyHeaders(302, "Found", headers, null, 0);
				return;
			}

			PageNode p = ctx.getPageMaker().getPageNode(Spider.pluginName, ctx);
			HTMLNode pageNode = p.outer;
			HTMLNode contentNode = p.content;

			P page = getPage();
			page.processPostRequest(request, contentNode);
			page.writeContent(request, contentNode);

			writeHTMLReply(ctx, 200, "OK", null, pageNode.generate());
		});
	}

	private void runWithContextClassLoader(ClassLoader classLoader, ToadletRunnable toadletRunnable) throws ToadletContextClosedException, IOException {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			toadletRunnable.run();
		} finally {
			Thread.currentThread().setContextClassLoader(origClassLoader);
		}
	}

	private interface ToadletRunnable {
		void run() throws ToadletContextClosedException, IOException;
	}

}
