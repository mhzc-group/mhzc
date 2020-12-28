package com.beauty.mhzc.wx.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.beauty.mhzc.db.service.UserService;
import com.beauty.mhzc.wx.config.JwtConfig;
import com.beauty.mhzc.wx.config.WxMaConfiguration;
import com.beauty.mhzc.db.domain.User;

import com.beauty.mhzc.wx.utils.ResponseUtil;
import io.swagger.annotations.Api;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xuan
 * @date 2020/12/21 22:14
 */

@RestController
@RequestMapping("/wx/user/{appId}")
@Api(value = "用户登陆与信息获取",tags = "用户登陆与信息获取")
public class WxAppletLoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 微信小程序端用户登陆api
     * 返回给小程序端 自定义登陆态 token
     */
    @PostMapping("/login")
    public Object wxAppletLoginApi(@PathVariable String appId, String code) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appId);

        if (code == null || "".equals(code)) {
            Map<String, String> result = new HashMap<>();
            result.put("msg", "缺少参数code或code不合法");
            return  ResponseUtil.fail(HttpStatus.BAD_REQUEST);
        } else {
            String token = null;
            try {
                WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
                // 先从本地数据库中查找用户是否存在
                User wxAccount = userService.queryByOid(session.getOpenid());
                if (wxAccount == null) {
                    wxAccount = new User();
                    //不存在就新建用户
                    wxAccount.setWeixinOpenid(session.getOpenid());
                    // 更新sessionKey和 登陆时间
                    wxAccount.setSessionKey(session.getSessionKey());
                    wxAccount.setLastLoginTime(LocalDateTime.now());
                    userService.add(wxAccount);
                }else {
                    // 更新sessionKey和 登陆时间
                    wxAccount.setSessionKey(session.getSessionKey());
                    wxAccount.setLastLoginTime(LocalDateTime.now());
                    userService.updateById(wxAccount);
                }

                //JWT 返回自定义登陆态 Token
                token = jwtConfig.createTokenByWxAccount(wxAccount);
            } catch (WxErrorException e) {
                return ResponseUtil.fail(e.getError().getErrorCode(),e.getError().getErrorMsg());
            }
            return ResponseUtil.ok(token);
        }
    }

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @PostMapping("/info")
    public Object info(@PathVariable String appId, String signature, String rawData, String encryptedData, String iv) throws UnsupportedEncodingException {
        final WxMaService wxService = WxMaConfiguration.getMaService(appId);
        Subject currentUser = SecurityUtils.getSubject();
        String  openId = (String)currentUser.getPrincipal();
        User user = userService.queryByOid(openId);
        String sessionKey=null;
        if(Objects.nonNull(user)){
            sessionKey=user.getSessionKey();
        }
        //用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return  ResponseUtil.fail(HttpStatus.BAD_REQUEST);
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        return  ResponseUtil.ok(userInfo);
    }


    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @PostMapping("/phone")
    public Object phone(@PathVariable String appId, String signature,
                        String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appId);
        Subject currentUser = SecurityUtils.getSubject();
        String  openId = (String)currentUser.getPrincipal();
        User user = userService.queryByOid(openId);
        String sessionKey=null;
        if(Objects.nonNull(user)){
            sessionKey=user.getSessionKey();
        }
        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return  ResponseUtil.fail(HttpStatus.BAD_REQUEST);
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        return  ResponseUtil.ok(phoneNoInfo);
    }


    @PostMapping("/sayHello")
    public Object sayHello() {
        Map<String, String> result = new HashMap<>();
        result.put("words", "hello World");
        return ResponseUtil.ok(result);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        String encode = URLEncoder.encode("{\"nickName\":\"xx_\uD83C\uDF52\",\"gender\":1,\"language\":\"zh_CN\",\"city\":\"Jixian\",\"province\":\"Tianjin\",\"country\":\"China\",\"avatarUrl\":\"https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIIxlu1wK2r7o90MxyrkhL0ibARtsZYl0I830Xia9MWibibqnK789qwIVrKGV3QUQuogGBwZibkoiczc2Bg/132\"}", "utf-8");
        String decode = URLDecoder.decode(encode, "utf-8");
        System.out.println(encode);
    }
}
