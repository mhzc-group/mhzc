package com.beauty.mhzc.core.storage;


import com.beauty.mhzc.core.util.CharUtil;
import com.beauty.mhzc.db.domain.Storage;
import com.beauty.mhzc.db.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 提供存储服务类，所有存储服务均由该类对外提供
 */
public class GenericStorageService {
    private String active;
    private GenericStorage genericStorage;
    @Autowired
    private StorageService storageService;
    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public GenericStorage getGenericStorage() {
        return genericStorage;
    }

    public void setGenericStorage(GenericStorage genericStorage) {
        this.genericStorage = genericStorage;
    }

    /**
     * 存储一个文件对象
     *
     * @param inputStream   文件输入流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @param fileName      文件索引名
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public Storage store(InputStream inputStream, long contentLength, String contentType, String fileName,String  appId) {
        String key = generateKey(fileName);
        String url = generateUrl(key);
        Storage storageInfo = new Storage();
        storageInfo.setName(fileName);
        storageInfo.setSize((int) contentLength);
        storageInfo.setType(contentType);
        storageInfo.setKey(key);
        storageInfo.setUrl(url);
        storageInfo.setAppId(appId);
        storageService.add(storageInfo);
        genericStorage.store(inputStream, contentLength, contentType, key);
        return storageInfo;
    }

    private String generateKey(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        Storage storageInfo = null;

        do {
            key = CharUtil.getRandomString(20) + suffix;
            storageInfo = storageService.findByKey(key);
        }
        while (storageInfo != null);

        return key;
    }

    public Stream<Path> loadAll() {
        return genericStorage.loadAll();
    }

    public Path load(String keyName) {
        return genericStorage.load(keyName);
    }

    public Resource loadAsResource(String keyName) {
        return genericStorage.loadAsResource(keyName);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void delete(String keyName) {
        storageService.deleteByKey(keyName);
        genericStorage.delete(keyName);
    }

    private String generateUrl(String keyName) {
        return genericStorage.generateUrl(keyName);
    }
}
