package com.meg.atable.configuration;

import com.meg.atable.lmt.service.TagReplaceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class StartupApplicationListener implements
        ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG
            = LogManager.getLogger(StartupApplicationListener.class);

    public static int counter;

    @Autowired
    TagReplaceService tagReplaceService;

    @Value("${service.tagservice.delete.tag.immediately:false}")
    boolean DELETE_IMMEDIATELY;

    @Override public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("Deleting tags to replace");
        if (!DELETE_IMMEDIATELY) {
            tagReplaceService.replaceAllTags();
        }

    }
}