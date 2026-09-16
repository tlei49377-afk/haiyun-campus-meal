package com.sky.aiservice.tools;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI 助手的菜品查询工具。
 * 每个 @Tool 方法都会作为「函数」暴露给大模型，由模型决定何时调用。
 */
@Component
@Slf4j
public class DishTools {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    //分类类型：1 菜品分类
    private static final Integer CATEGORY_TYPE_DISH = 1;

    //单次最多返回的条数，避免把过多数据塞给模型浪费 token
    private static final int PAGE_SIZE = 10;

    @Tool("查询菜品信息。可以根据菜品名称、分类名称、售卖状态查询菜品列表，"
            + "返回菜品的名称、所属分类、价格和售卖状态。"
            + "当用户询问某个菜品、某个分类下有哪些菜、某道菜的价格时，调用此工具。")
    public String queryDish(
            @P(value = "菜品名称关键字，支持模糊匹配。用户没有提到具体菜名时传空字符串", required = false)
            String dishName,

            @P(value = "菜品分类名称，例如“酒水饮料”“蜀味烤鱼”“特色蒸菜”。用户没有提到分类时传空字符串", required = false)
            String categoryName,

            @P(value = "菜品的售卖状态：1 表示起售（在售），0 表示停售（已下架）。用户没有提到状态时传 -1", required = false)
            Integer status) {

        log.info("AI 工具调用 -> queryDish(dishName={}, categoryName={}, status={})",
                dishName, categoryName, status);

        try {
            DishPageQueryDTO dto = new DishPageQueryDTO();
            dto.setPage(1);
            dto.setPageSize(PAGE_SIZE);

            if (isNotBlank(dishName)) {
                dto.setName(dishName.trim());
            }

            // 用户说的是「分类名称」，而 DTO 需要的是「分类 id」，这里必须做一次转换
            if (isNotBlank(categoryName)) {
                Long categoryId = findDishCategoryId(categoryName.trim());
                if (categoryId == null) {
                    return "没有找到名为「" + categoryName + "」的菜品分类，请确认分类名称是否正确。";
                }
                dto.setCategoryId(categoryId.intValue());
            }

            if (status != null && status != -1) {
                dto.setStatus(status);
            }

            PageResult pageResult = dishService.page(dto);

            @SuppressWarnings("unchecked")
            List<DishVO> records = pageResult.getRecords();

            if (records == null || records.isEmpty()) {
                return "没有查询到符合条件的菜品。";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("共查询到 ").append(pageResult.getTotal())
              .append(" 道菜品，以下是最多 ").append(records.size()).append(" 条结果：\n");

            int index = 1;
            for (DishVO dish : records) {
                sb.append(index++).append(". ")
                  .append(dish.getName())
                  .append(" | 分类：").append(dish.getCategoryName() == null ? "未知" : dish.getCategoryName())
                  .append(" | 价格：").append(dish.getPrice()).append(" 元")
                  .append(" | 状态：").append(toStatusText(dish.getStatus()))
                  .append("\n");
            }
            return sb.toString();

        } catch (Exception e) {
            // 工具内部必须自行兜底：抛异常会中断整轮对话，用户只会看到报错
            log.error("AI 工具 queryDish 执行失败", e);
            return "查询菜品时发生错误：" + e.getMessage();
        }
    }

    /**
     * 把分类名称解析成分类 id。
     * 先精确匹配，再模糊匹配（“热菜” 与 “热菜类” 这种能对上）。
     * 注意：底层 mapper 只返回「启用状态」的分类。
     */
    private Long findDishCategoryId(String categoryName) {
        List<Category> categories = categoryService.list(CATEGORY_TYPE_DISH);
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        for (Category category : categories) {
            if (categoryName.equals(category.getName())) {
                return category.getId();
            }
        }
        for (Category category : categories) {
            String name = category.getName();
            if (name != null && (name.contains(categoryName) || categoryName.contains(name))) {
                return category.getId();
            }
        }
        return null;
    }

    private String toStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "起售" : "停售";
    }

    private boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
