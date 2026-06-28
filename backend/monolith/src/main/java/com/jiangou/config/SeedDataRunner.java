package com.jiangou.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.dto.CreateArticleDTO;
import com.jiangou.article.service.ArticleService;
import com.jiangou.category.entity.CategoryEntity;
import com.jiangou.category.mapper.CategoryMapper;
import com.jiangou.friendlink.entity.FriendLinkEntity;
import com.jiangou.friendlink.mapper.FriendLinkMapper;
import com.jiangou.search.service.SearchIndexService;
import com.jiangou.system.entity.SystemSettingEntity;
import com.jiangou.system.mapper.SystemSettingMapper;
import com.jiangou.note.dto.NoteDTO;
import com.jiangou.note.service.NoteService;
import com.jiangou.project.dto.ProjectDTO;
import com.jiangou.project.service.ProjectService;
import com.jiangou.snippet.dto.SnippetDTO;
import com.jiangou.snippet.service.SnippetService;
import com.jiangou.tag.entity.TagEntity;
import com.jiangou.tag.mapper.TagMapper;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
@Profile("dev")
public class SeedDataRunner {

    @Bean
    CommandLineRunner seed(UserMapper userMapper, PasswordEncoder passwordEncoder,
                           CategoryMapper categoryMapper, TagMapper tagMapper,
                           ArticleService articleService, SnippetService snippetService,
                           NoteService noteService, ProjectService projectService,
                           FriendLinkMapper friendLinkMapper,
                           SystemSettingMapper systemSettingMapper,
                           SearchIndexService searchIndexService,
                           com.jiangou.user.service.RoleService roleService) {
        return args -> {
            UserEntity admin = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getUsername, "admin"));
            if (admin == null) {
                admin = new UserEntity();
                admin.setUsername("admin");
                admin.setDisplayName("管理员");
                admin.setEmail("admin@jiangou.local");
                admin.setEmailVerified(true);
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setStatus("active");
                admin.setTokenVersion(0);
                admin.setMetadata("{}");
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                userMapper.insert(admin);
            }
            roleService.ensureRole("ADMIN", "管理员");
            roleService.assignRole(admin.getId(), "ADMIN");

            CategoryEntity category = categoryMapper.selectOne(new LambdaQueryWrapper<CategoryEntity>()
                    .eq(CategoryEntity::getSlug, "engineering"));
            if (category == null) {
                category = new CategoryEntity();
                category.setName("工程实践");
                category.setSlug("engineering");
                category.setDescription("工程与架构");
                category.setSortOrder(0);
                category.setPostCount(0);
                category.setMetadata("{}");
                category.setCreatedAt(LocalDateTime.now());
                category.setUpdatedAt(LocalDateTime.now());
                categoryMapper.insert(category);
            }

            TagEntity tag = tagMapper.selectOne(new LambdaQueryWrapper<TagEntity>()
                    .eq(TagEntity::getSlug, "java"));
            if (tag == null) {
                tag = new TagEntity();
                tag.setName("Java");
                tag.setSlug("java");
                tag.setColor("#7b5f3a");
                tag.setUsageCount(0);
                tag.setMetadata("{}");
                tag.setCreatedAt(LocalDateTime.now());
                tag.setUpdatedAt(LocalDateTime.now());
                tagMapper.insert(tag);
            }

            if (articleService.listPublic(1, 1, null, null, null).getTotal() == 0) {
                CreateArticleDTO dto = new CreateArticleDTO();
                dto.setTitle("欢迎来到渐构");
                dto.setSlug("welcome-to-jiangou");
                dto.setSummary("渐次构建，理解计算机世界。");
                dto.setContentMd("# 欢迎来到渐构\n\n这是第一篇示例文章。\n\n## 快速开始\n\n阅读更多内容。\n\n```java\npublic class Hello {\n  public static void main(String[] args) {\n    System.out.println(\"Hello JianGou\");\n  }\n}\n```");
                dto.setCategoryId(category.getId());
                dto.setTagIds(Arrays.asList(tag.getId()));
                com.jiangou.article.vo.ArticleDetailVO created = articleService.create(dto, admin.getId());
                articleService.publish(created.getId(), admin.getId());
            }

            if (snippetService.listPublic(1, 1, null).getTotal() == 0) {
                SnippetDTO snippet = new SnippetDTO();
                snippet.setTitle("Hello Java");
                snippet.setSlug("hello-java");
                snippet.setLanguage("java");
                snippet.setCode("System.out.println(\"Hello JianGou\");");
                snippet.setDescriptionMd("一行 Java 示例");
                snippetService.create(snippet, admin.getId());
            }

            if (noteService.listPublic(1, 1).getTotal() == 0) {
                NoteDTO note = new NoteDTO();
                note.setContentMd("开始搭建渐构博客，渐次构建。");
                com.jiangou.note.vo.NoteVO createdNote = noteService.create(note, admin.getId());
                noteService.publish(createdNote.getId());
            }

            if (projectService.listPublic().isEmpty()) {
                ProjectDTO project = new ProjectDTO();
                project.setOwner("jiangou");
                project.setRepo("blog");
                project.setName("JianGou Blog");
                project.setDescription("渐构个人技术博客平台");
                project.setPinned(true);
                projectService.create(project);
            }

            if (friendLinkMapper.selectCount(new LambdaQueryWrapper<FriendLinkEntity>()) == 0) {
                FriendLinkEntity link = new FriendLinkEntity();
                link.setName("示例友链");
                link.setUrl("https://example.com");
                link.setDescription("示例站点");
                link.setStatus("approved");
                link.setSortOrder(0);
                link.setMetadata("{}");
                link.setCreatedAt(LocalDateTime.now());
                link.setUpdatedAt(LocalDateTime.now());
                friendLinkMapper.insert(link);
            }

            if (systemSettingMapper.selectById("siteTitle") == null) {
                SystemSettingEntity title = new SystemSettingEntity();
                title.setKey("siteTitle");
                title.setValue("渐构");
                title.setDescription("站点标题");
                title.setIsPublic(true);
                title.setUpdatedAt(LocalDateTime.now());
                systemSettingMapper.insert(title);
            }

            if (systemSettingMapper.selectById("siteLaunchDate") == null) {
                SystemSettingEntity launch = new SystemSettingEntity();
                launch.setKey("siteLaunchDate");
                launch.setValue(LocalDateTime.now().toLocalDate().toString());
                launch.setDescription("站点上线日期");
                launch.setIsPublic(true);
                launch.setUpdatedAt(LocalDateTime.now());
                systemSettingMapper.insert(launch);
            }

            if (systemSettingMapper.selectById("siteDescription") == null) {
                SystemSettingEntity desc = new SystemSettingEntity();
                desc.setKey("siteDescription");
                desc.setValue("渐构是个人技术知识沉淀平台，融合 Blog、Wiki、代码片段与项目陈列。");
                desc.setDescription("站点简介");
                desc.setIsPublic(true);
                desc.setUpdatedAt(LocalDateTime.now());
                systemSettingMapper.insert(desc);
            }

            if (systemSettingMapper.selectById("siteSubtitle") == null) {
                SystemSettingEntity subtitle = new SystemSettingEntity();
                subtitle.setKey("siteSubtitle");
                subtitle.setValue("渐次构建，理解计算机世界");
                subtitle.setDescription("站点副标题");
                subtitle.setIsPublic(true);
                subtitle.setUpdatedAt(LocalDateTime.now());
                systemSettingMapper.insert(subtitle);
            }

            if (systemSettingMapper.selectById("guestbookTargetId") == null) {
                SystemSettingEntity guestbook = new SystemSettingEntity();
                guestbook.setKey("guestbookTargetId");
                guestbook.setValue("1");
                guestbook.setDescription("留言板评论 targetId");
                guestbook.setIsPublic(true);
                guestbook.setUpdatedAt(LocalDateTime.now());
                systemSettingMapper.insert(guestbook);
            }

            searchIndexService.rebuildAll();
        };
    }
}
