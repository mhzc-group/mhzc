package com.beauty.mhzc.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xuan
 * @date 2020/10/28 19:47
 */

@Data
public class MallVO {
    private String id;
    private String mallName;
    private String phone;
    private String avatar;
    private String appId;
    private LocalDateTime createOn;
    private String createBy;
    private LocalDateTime updateOn;
    private String updateBy;
    private Boolean deleted;
    private List<String> managerNames;
}
