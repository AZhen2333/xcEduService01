package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 分页查询页面
     *
     * @param page
     * @param size
     * @param queryPageRequest
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        // 条件匹配器
        // 页面模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains());
        // 条件值
        CmsPage cmsPage = new CmsPage();
        // 站点id
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //页面别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        // 页面类型
        if (queryPageRequest.getPageType() != null) {
            cmsPage.setPageType(queryPageRequest.getPageType());
        }
        // 创建实例条件
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        // 页码
        page = page - 1;
        // 分页对象
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        // 返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /**
     * 添加页面信息
     *
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage) {
        // 校验页面是否存在
        CmsPage one = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (one != null) {
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        // 不存在，新增
        if (one == null) {
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 根据id查询页面
     *
     * @param id
     * @return
     */
    public CmsPage getById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public CmsPageResult update(String id, CmsPage cmsPage) {
        CmsPage one = this.getById(id);
        if (one != null) {
            // 更新模版id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                return new CmsPageResult(CommonCode.SUCCESS, save);
            }
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    //删除页面
    public ResponseResult delete(String id) {
        CmsPage one = this.getById(id);
        if (one != null) {
            //删除页面
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 页面静态化
     *
     * @param pageId
     * @return
     */
    public String getPageHtml(String pageId) {
        // 获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        // 获取页面模版
        String templateContent = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(templateContent)) {
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        // 执行静态化
        String html = generateHtml(templateContent, model);
        if (StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    /**
     * 执行静态化
     *
     * @param template
     * @param model
     * @return
     */
    public String generateHtml(String template, Map model) {
        try {
            // 生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            // 模版加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("tenplate", template);
            // 配置模版加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            // 获取模版
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取页面模版
     *
     * @param pageId
     * @return
     */
    public String getTemplateByPageId(String pageId) {
        // 查询页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            // 页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        // 页面模版
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            // 页面模版为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            // 模版文件id（上传返回的id）
            String templateFileId = cmsTemplate.getTemplateFileId();
            // 取出模版文件内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("id").is(templateFileId)));
            // 打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            // 创建GridFsResource
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            try {
                String content = org.apache.commons.io.IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取页面模型数据
     *
     * @param pageId
     * @return
     */
    public Map getModelByPageId(String pageId) {
        // 查询页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            // 页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        // 取出dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    /**
     * 发布页面
     *
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId) {
        // 执行静态化
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        // 保存静态文件
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        // 发送消息到rabbitMq
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 发送页面发布消息
     *
     * @param pageId
     */
    private void sendPostPage(String pageId) {
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        Map<String, String> magMap = new HashMap<>();
        magMap.put("pageId", pageId);
        // 消息内容
        String msg = JSON.toJSONString(magMap);
        // 获取站点id作为routingkey
        String siteId = cmsPage.getSiteId();
        // 发布消息
        this.rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, msg);
    }

    /**
     * 保存静态页面内容
     *
     * @param pageId
     * @param content
     * @return
     */
    public CmsPage saveHtml(String pageId, String content) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();
        // 存储之前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        // 保存html文件到GridFs
        InputStream inputStream = IOUtils.toInputStream(content);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        // 文件id
        String fileId = objectId.toString();
        // 将文件id存储到cmsPage中
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;

    }

    /**
     * 添加页面，如果已存在则更新页面 
     *
     * @param cmsPage
     * @return
     */
    public CmsPageResult save(CmsPage cmsPage) {
        CmsPage one = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (one != null) {
            return this.update(one.getPageId(), cmsPage);
        } else {
            return this.add(cmsPage);
        }
    }
}
