package com.shine.integrationtestcover.controller;


import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.GraphService;
import com.shine.integrationtestcover.service.codeParse.MethodVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class FileParserController {

    @Autowired
    GraphService graphService;

    @Autowired
    BaseConfig baseConfig;

    @RequestMapping(value = "/relation",method = RequestMethod.GET)
    public HashMap<String, Object> getInvokeRelationship(@RequestParam String name, @RequestParam String packages){
        MethodVisitor.packageNames = packages.isEmpty()? new String[]{""}: packages.split("\n");
        graphService.setFilename(name);
        graphService.setPath(baseConfig.getUploadedFilePath());
        graphService.initiate();
        ArrayList edges=graphService.getEdges();
        ArrayList<HashMap<String, Object>> vertex=graphService.getVertex();
        HashMap<String, Object> result = new HashMap<>();
        result.put("nodes", vertex);
        result.put("links", edges);
        result.put("classes", MethodVisitor.classes.toArray());
        result.put("classMethodMap", MethodVisitor.methods);
        MethodVisitor.classes = new HashSet<>();
        MethodVisitor.methods = new HashMap<>();
        return result;

    }
}
