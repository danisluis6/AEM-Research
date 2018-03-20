package com.moviecomponent.moviecompoment.core.service.core;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieQuery implements MovieInterface {
    Logger logger = LoggerFactory.getLogger(MovieQuery.class);

    private Session session;

    // Inject a Sling ResourceResolverFactory
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder builder;

    @Override
    public List getUpcomingMovies(int numberOfMovies, String pathToMovies) {

        List<String> hyperLinks = new ArrayList();

        try {

            logger.info("Getting Ready to create SESSION!!");

            // Invoke the adaptTo method to create a Session
            ResourceResolver resourceResolver = resolverFactory
                    .getAdministrativeResourceResolver(null);
            session = resourceResolver.adaptTo(Session.class);

            // create query description as hash map
            Map<String, String> map = new HashMap<String, String>();
            map.put("path", pathToMovies);
            map.put("type", Constant.SEARCH_TYPE);
            map.put("p.limit", String.valueOf(numberOfMovies)); // same as
            // query.setHitsPerPage(20)
            // below

            Query query = builder.createQuery(PredicateGroup.create(map),
                    session);
            query.setStart(0);

            SearchResult result = query.getResult();

            logger.info("Did we get the result");

            // paging metadata
            int hitsPerPage = result.getHits().size(); // 20 (set above) or
            // lower
            logger.info("Result :"+hitsPerPage);

            // iterating over the results
            for (Hit hit : result.getHits()) {
                String path = hit.getPath();
                // Create a result element
                logger.info(path);
                hyperLinks.add(path);

            }
            // close the session
        } catch (Exception e) {
            this.logger.info("Something went wrong with session .. {}", e);
        } finally {
            session.logout();
            return hyperLinks;
        }

    }
}