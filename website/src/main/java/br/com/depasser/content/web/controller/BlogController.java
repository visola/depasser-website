package br.com.depasser.content.web.controller;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

//import viecili.jrss.generator.RSSFeedGenerator;
//import viecili.jrss.generator.RSSFeedGeneratorFactory;
//import viecili.jrss.generator.elem.Channel;
//import viecili.jrss.generator.elem.Item;
//import viecili.jrss.generator.elem.RSS;
import br.com.depasser.content.Content;
import br.com.depasser.content.Option;
import br.com.depasser.content.Template;
import br.com.depasser.content.comparators.DateFieldComparator;
import br.com.depasser.content.comparators.DescendingStringComparator;
import br.com.depasser.content.field.BooleanField;
import br.com.depasser.content.field.DateField;
import br.com.depasser.content.field.LongTextField;
import br.com.depasser.content.field.MultiValueField;
import br.com.depasser.content.field.ParentField;
import br.com.depasser.content.field.TextField;
import br.com.depasser.content.service.TemplateService;
import br.com.depasser.content.web.service.BlogService;
import br.com.depasser.web.spring.AbstractController;

@Controller
@RequestMapping("/blog")
public class BlogController extends AbstractController {
	
	/**
	 * Limit to reload all blog posts.
	 */
	private static final long CACHE_LIMITE = 1000 * 60 * 60 * 2; // 2 hours
	
	/**
	 * How many blog posts per page.
	 */
	private static final int PAGE_SIZE = 5;

	@Autowired
	private BlogService blogService;
	
	@Autowired
	private TemplateService templateService;
	
	private long lastUpdated = 0;
	
	/**
	 * A map where the key is the language and the value is a list
	 * with all blog posts for the specified language.
	 */
	private Map<String, List<Content>> allBlogPosts;
	
	private Map<Content, Map<String, Object>> summaries = new HashMap<Content, Map<String, Object>>();
	
	// @RequestMapping("/clearCache")
	public synchronized ModelAndView clearCache () throws Exception {
		allBlogPosts = null;
		lastUpdated = 0;
		summaries = new HashMap<Content, Map<String, Object>>();
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("message", "Cache cleared.");
		return mv;
	}
	
	@RequestMapping("/load")
	public ModelAndView getBlogPost(Integer id, Locale locale) throws Exception {
		Content blogPost = getPost(id, locale);
		
		ModelAndView mv = new ModelAndView();
		
		if (blogPost == null) {
			mv.addObject("message", ResourceBundle.getBundle("keys", locale).getObject("blog.text.postNotFound"));
		} else {
			mv.addObject("id", blogPost.getId());
			mv.addObject("language", blogPost.getLanguage());
			mv.addObject("title", blogPost.getTitle());
			mv.addObject("content", blogPost.getField("Content").getValue());
			mv.addObject("date", blogPost.getField("Created").getValue());
			mv.addObject("by", blogPost.getField("Created By").getValue());
			mv.addObject("tags", summarizeContent(blogPost).get("tags"));
			mv.addObject("categories", summarizeContent(blogPost).get("categories"));
			
			List<Map<String, Object>> commentsList = new ArrayList<Map<String, Object>>();
			mv.addObject("comments", commentsList);
			
			List<Content> comments = new ArrayList<Content>(blogService.getChildContent(blogPost.getIdentification()));
			Collections.sort(comments, new DateFieldComparator("Created", false)); // sort by date
			for (Content comment : comments) {
				if ( ( (BooleanField) comment.getField("Active") ).getValue() == true) {
					Map<String, Object> c = new HashMap<String, Object>();
					commentsList.add(c);
				
					c.put("title", comment.getTitle());
					c.put("date", comment.getField("Created").getValue());
					c.put("by", comment.getField("Created By").getValue());
					c.put("content", comment.getField("Content").getValue());
				}
			}
		}
		
		return mv;
	}

	private Content getPost(Integer id, Locale locale) throws Exception {
		List<Content> posts = getBlogPosts(locale);
		
		Content blogPost = null;
		for (Content c : posts) {
			if (c.getId() == id) {
				blogPost = c;
				break;
			}
		}
		return blogPost;
	}
	
	@RequestMapping("/comment")
	public ModelAndView saveComment(Integer postId, String postLanguage, String name, String commentText, Locale locale) throws Exception {
		// Get post
		Content blogPost = getPost(postId, locale);
		
		// Load template
		Template commentTemplate = templateService.loadTemplate(4);
		
		// Create content object
		Content comment = commentTemplate.createEmptyContent();
		comment.setTitle(blogPost.getTitle());
		comment.setLanguage(locale.toString());
		
		// Remove HTML from name and comment
		name = name.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		commentText = commentText.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		
		// Set values
		( (DateField) comment.getField("Created") ).setValue(Calendar.getInstance());
		( (TextField) comment.getField("Created By") ).setValue(name);
		( (LongTextField) comment.getField("Content") ).setValue(commentText);
		( (ParentField) comment.getField("Parent") ).setValue(blogPost);
		( (BooleanField) comment.getField("Active") ).setValue(false);
		
		// Save it
		comment = blogService.saveContent(comment);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("message", ResourceBundle.getBundle("keys", locale).getObject("blog.comments.message.success"));
		return mv;
	}
	
	private List<Content> getBlogPosts(Locale locale) throws Exception {
		return getBlogPosts(locale, null, null, null, null);
	}
	
	private List<Content> getBlogPosts(Locale locale, Integer year, Integer month, String tag, String category) throws Exception {
		loadBlogPosts();
		List<Content> blogPosts = allBlogPosts.get(locale.toString());
		
		// If none found, load for default language
		if (blogPosts == null) {
			Locale defaultLocale = new Locale("pt", "br");
			if (defaultLocale.equals(locale)) {
				// If already default locale, return an empty list
				return new ArrayList<Content>(); 
			}
			return getBlogPosts(defaultLocale, year, month, tag, category);
		}
		
		// Filter 
		List<Content> result = new ArrayList<Content>();
		for (Content blogPost : blogPosts) {
			Calendar updated = (Calendar) blogPost.getField("Updated").getValue();
			int postYear = updated.get(Calendar.YEAR);
			int postMonth = updated.get(Calendar.MONTH);
			
			boolean add = true;
			
			if (year != null && year != postYear) add = false;
			if (month != null && month != postMonth) add = false;
			
			MultiValueField categories = (MultiValueField) blogPost.getField("Blog Categories");
			if (category != null && !categories.hasOptionNamed(category)) add = false;
			
			MultiValueField tags = (MultiValueField) blogPost.getField("Tags");
			if (tag != null && !tags.hasOptionNamed(tag)) add = false;
			
			if (add) {
				result.add(blogPost);
			}
		}
		
		return result;
	}
	
	@RequestMapping("/categories")
	public ModelAndView getCategories(Locale locale) throws Exception {
		Map<String, Integer> categories = new TreeMap<String, Integer>();
		
		for (Content blog : getBlogPosts(locale)) {
			MultiValueField blogCategories = (MultiValueField) blog.getField("Blog Categories");
			
			for (Option blogCategory : blogCategories.getValues()) {
				Integer count = categories.get(blogCategory.getName());
				if (count == null) {
					categories.put(blogCategory.getName(), 1);
				} else {
					categories.put(blogCategory.getName(), count + 1);
				}
			}
		}
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("categories", categories);
		return mv;
	}

	public BlogService getBlogService() {
		return blogService;
	}
	
	@RequestMapping("/dates")
	public ModelAndView getDates(Locale locale) throws Exception {
		List<Content> posts = getBlogPosts(locale);
		
		Map<String, Map<String,Integer>> countByDate = new TreeMap<String, Map<String,Integer>>(new DescendingStringComparator());
		
		for (Content blogPost : posts) {
			Calendar updated = ( (DateField) blogPost.getField("Updated") ).getValue();
			
			String year = Integer.toString(updated.get(Calendar.YEAR));
			String month = Integer.toString(updated.get(Calendar.MONTH));
			
			Map<String, Integer> monthMap = countByDate.get(year);
			if (monthMap == null) {
				monthMap = new TreeMap<String, Integer>(new DescendingStringComparator());
				countByDate.put(year, monthMap);
			}
			
			Integer count = monthMap.get(month);
			if (count == null) {
				monthMap.put(month, 1);
			} else {
				monthMap.put(month, count + 1);
			}
		}
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("dates", countByDate);
		return mv;
	}
	
	@RequestMapping("/posts")
	public ModelAndView getPosts(Locale locale, Integer actualPage, String tag, String category, Integer month, Integer year) throws Exception {
		loadBlogPosts();
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (actualPage == null) {
			actualPage = 0;
		}
		
		List<Content> blogPosts = getBlogPosts(locale, year, month, tag, category);
		int start = PAGE_SIZE * actualPage;
		
		int end = start + PAGE_SIZE;
		if (end > blogPosts.size()) end = blogPosts.size();
		
		for (int i = start; i < end; i++) {
			Content blogPost = blogPosts.get(i);
			Map<String, Object> info = summarizeContent(blogPost);
			result.add(info);			
		}
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("content", result);
		mv.addObject("size", blogPosts.size());
		mv.addObject("pageSize", PAGE_SIZE);
		mv.addObject("actualPage", actualPage);;
		mv.addObject("minPage", 0);
		mv.addObject("maxPage", Math.floor( (double) (blogPosts.size() - 1) / (double) PAGE_SIZE));
		return mv;
	}
	
	@RequestMapping("/tags")
	public ModelAndView getTags(Locale locale) throws Exception {
		loadBlogPosts();
		
		Map<String, Integer> tags = new TreeMap<String, Integer>();
		
		for (Content blog : getBlogPosts(locale)) {
			MultiValueField blogTags = (MultiValueField) blog.getField("Tags");
			
			for (Option blogTag : blogTags.getValues()) {
				Integer count = tags.get(blogTag.getName());
				if (count == null) {
					tags.put(blogTag.getName(), 1);
				} else {
					tags.put(blogTag.getName(), count + 1);
				}
			}
		}
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("tags", tags);
		return mv;
	}
	
	private synchronized void loadBlogPosts() throws Exception {
		if (allBlogPosts == null || System.currentTimeMillis() - lastUpdated > CACHE_LIMITE) {
			// Load from database
			List<Content> temp = blogService.findContentByType(3);
			
			allBlogPosts = new HashMap<String, List<Content>>();
			
			// Map by language
			for (Content blog : temp) {
				List<Content> posts = allBlogPosts.get(blog.getLanguage());
				if (posts == null) {
					posts = new ArrayList<Content>();
					allBlogPosts.put(blog.getLanguage(), posts);
				}
				posts.add(blog);
			}
			
			// Sort all lists
			Comparator<Content> updatedComparator = new DateFieldComparator("Updated", false); 
			for (String language : allBlogPosts.keySet()) {
				Collections.sort(allBlogPosts.get(language), updatedComparator);
			}
			
			lastUpdated = System.currentTimeMillis();
		}
	}

	public void setContentService(BlogService blogService) {
		this.blogService = blogService;
	}

	private Map<String, Object> summarizeContent(Content blogPost) throws Exception {
		// Check cache
		Map<String, Object> info = summaries.get(blogPost);
		
		if (info == null) {
			info = new HashMap<String, Object>();
			summaries.put(blogPost, info);
			
			info.put("title", blogPost.getTitle());
			info.put("id", blogPost.getId());
			info.put("date", blogPost.getField("Created").getValue());
			info.put("abstract", blogPost.getField("Abstract").getValue());
			info.put("by", blogPost.getField("Created By").getValue());
			
			List<String> tags = new ArrayList<String>();
			info.put("tags", tags);
			for (Option option : ( (MultiValueField) blogPost.getField("Tags")).getValues() ) {
				tags.add(option.getName());
			}
			
			List<String> categories = new ArrayList<String>();
			info.put("categories", categories);
			for (Option option : ( (MultiValueField) blogPost.getField("Blog Categories")).getValues() ) {
				categories.add(option.getName());
			}
		}
		
		info.put("comments", blogService.getCommentCount(blogPost.getIdentification()));
			
		return info;
	}
	
//	@RequestMapping("/rss")
//	public void getRss(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
//		response.setContentType("text/xml");
//
//		// Create the feed
//		RSS rss = new RSS();
//		Channel channel = new Channel("Dépasser Escola de Tecnologia", "http://www.depasser.com.br", "Últimas notícias sobre a escola, cursos, artigos e tutoriais.");
//		rss.addChannel(channel);
//
//		List<Content> blogPosts = getBlogPosts(locale);
//
//		// Add last 10 posts
//		int last = 10;
//		if (last > blogPosts.size()) {
//			last = blogPosts.size();
//		}
//		for (int i = 0; i < last; i++) {
//			Content blogPost = blogPosts.get(i);
//
//			String abs = ( (LongTextField) blogPost.getField("Abstract") ).getValue();
//			Calendar pubDate = ( (DateField) blogPost.getField("Updated") ).getValue();
//
//			StringBuffer link = request.getRequestURL();
//			int indexOf = link.indexOf("blog/rss");
//
//			link.setLength(indexOf);
//			link.append("index.do#{\"page\":\"blog\",\"postId\":");
//			link.append(blogPost.getId());
//			link.append("}");
//
//			Item item = new Item(blogPost.getTitle(), abs);
//			item.setPubDate(pubDate.getTime());
//			item.setGuid(item.new Guid(blogPost.getId() + "," + blogPost.getLanguage(), true));
//			item.setLink(link.toString());
//
//			channel.addItem(item);
//		}
//
//		// Write to client
//		RSSFeedGenerator generator = RSSFeedGeneratorFactory.getDefault();
//		Writer out = response.getWriter();
//		out.write(generator.generateAsString(rss));
//		out.flush();
//	}
		
}