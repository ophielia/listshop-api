package com.meg.listshop.configuration;

import com.meg.listshop.lmt.service.tag.TagReplaceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class StartupApplicationListener implements
        ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG
            = LogManager.getLogger(StartupApplicationListener.class);

    TagReplaceService tagReplaceService;

    @Autowired
    public StartupApplicationListener(TagReplaceService tagReplaceService) {
        this.tagReplaceService = tagReplaceService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        LOG.info("Deleting tags to replace");
        tagReplaceService.replaceAllTags();

    }
}