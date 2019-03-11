package com.mw.jsp.details.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Michael Wall
 */
@Component(
		property = {
			"dispatcher=INCLUDE", "dispatcher=INCLUDE",
			"osgi.http.whiteboard.filter.name=com.mw.jsp.details.filter.JSPDetailsFilter",
			"osgi.http.whiteboard.filter.pattern=*.jsp", "osgi.http.whiteboard.filter.dispatcher=INCLUDE",
			"osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=*)", "servlet-context-name=",
			"servlet-filter-name=jsp-spi", "url-pattern=*.jsp"
		},
		scope = ServiceScope.PROTOTYPE, service = Filter.class
	)
public class JspDetailsFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {
		
		String id = UUID.randomUUID().toString();

		String jspDetails = getJspDetails(request);
		
		if (_log.isDebugEnabled()) {
			_log.debug(id + ", " + jspDetails);			
		}
		
		String start = "\n<!-- MW-JSP-START-" + id + " " + jspDetails + " -->\n";
		String end = "\n<!-- MW-JSP-END-" + id + " -->\n";
		
		try {
			PrintWriter printWriter = response.getWriter();

			printWriter.write(start);
		}
		catch (IllegalStateException ise) {
			ServletOutputStream servletOutputStream = response.getOutputStream();

			servletOutputStream.write(start.getBytes());
		}

		filterChain.doFilter(request, response);

		try {
			PrintWriter printWriter = response.getWriter();

			printWriter.write(end);
		}
		catch (IllegalStateException ise) {
			ServletOutputStream servletOutputStream = response.getOutputStream();

			servletOutputStream.write(end.getBytes());
		}		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	private String getJspDetails(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;

		ServletContext servletContext = httpServletRequest.getServletContext();

		BundleContext bundleContext = (BundleContext)servletContext.getAttribute("osgi-bundlecontext");

		String bundleSymbolicName = "";
		String bundleVersion = "";
		String bundleLocation = "";
		
		if (bundleContext != null) {
			Bundle bundle = bundleContext.getBundle();

			bundleSymbolicName = bundle.getSymbolicName();
			
			bundleLocation = bundle.getLocation();
			
			bundleVersion = bundle.getVersion().toString();
		}
		
		StringBuilder jspSB = new StringBuilder();
		
		if (Validator.isNotNull(bundleSymbolicName)) {
			jspSB.append(bundleSymbolicName);
		}
		
		if (Validator.isNotNull(bundleVersion)) {
			if (jspSB.length() > 0) jspSB.append(" ");
			
			jspSB.append(bundleVersion);
		}
		
		if (jspSB.length() > 0) jspSB.append(" ");
		
		jspSB.append(httpServletRequest.getServletPath() + "->" + httpServletRequest.getAttribute("javax.servlet.include.servlet_path"));
		
		jspSB.append(" location: " + bundleLocation);
		
		return jspSB.toString();
	}
	
	private static final Log _log = LogFactoryUtil.getLog(
			JspDetailsFilter.class);
	
}