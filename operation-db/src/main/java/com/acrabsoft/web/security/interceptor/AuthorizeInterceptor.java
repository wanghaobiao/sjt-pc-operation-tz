//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.acrabsoft.web.security.interceptor;

import com.acrabsoft.web.annotation.Interceptor;
import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.interceptor.WebAppInterceptor;
import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.security.handler.AuthorizeHandlerInterceptor;
import com.acrabsoft.web.utils.JwtUtil;
import com.acrabsoft.web.utils.ResponseUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.utils.security.aes.AESCBCPKCS7;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;

@Component
@Interceptor(
        order = 10
)
public class AuthorizeInterceptor extends WebAppInterceptor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired(
            required = false
    )
    private AuthorizeHandlerInterceptor authorhander;
    @Value("${config.aes.issecurity}")
    private boolean aes;
    @Value("${config.aes.skey}")
    private String skey;
    @Value("${config.aes.iv}")
    private String iv;
    @Value("${config.security.escpe_urls}")
    private String escpe_urls;

    public AuthorizeInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (StringUtils.isNotEmpty(this.escpe_urls)) {
            String[] var8;
            int var7 = (var8 = this.escpe_urls.split(",")).length;

            for(int var6 = 0; var6 < var7; ++var6) {
                String s = var8[var6];
                if (uri.indexOf(s) != -1) {
                    return true;
                }
            }
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(NotAuthorize.class)) {
                return true;
            }

            NotAuthorize notAuthorize = (NotAuthorize)AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), NotAuthorize.class);
            if (notAuthorize != null) {
                return true;
            }
        }

        Result result = null;
        String headStr = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(headStr) && headStr.startsWith("Bearer ")) {
            boolean b = false;
            String subject = null;
            BasicUser user = null;

            try {
                if(subject == null){
                    return true;
                }
                String token = headStr.substring(7);
                Claims claims = JwtUtil.parseJWT(token);
                subject = claims.getSubject();
                if (StringUtils.isEmpty(subject)) {
                    this.logger.error("解码后数据为空");
                    result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息为空", "解码后数据为空");
                } else if (this.aes) {
                    AESCBCPKCS7 aes = new AESCBCPKCS7();
                    subject = aes.decrypt(subject, "UTF-8", this.skey, this.iv);
                    b = true;
                } else {
                    b = true;
                }

                user = (BasicUser)JSON.parseObject(subject, BasicUser.class);
            } catch (ExpiredJwtException var13) {
                this.logger.error("登录信息 超过有效期", var13);
                result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息已过期", "登录信息超过有效期");
            } catch (SignatureException var14) {
                this.logger.error("登录信息  被篡改！", var14);
                result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息不合法", "登录信息 被篡改");
            } catch (UnsupportedEncodingException var15) {
                this.logger.error("请求头认证信息解码时编码错误!", var15);
                result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息不合法", "请求头认证信息不合法，请求头认证信息解码时编码错误");
            } catch (GeneralSecurityException var16) {
                this.logger.error("请求头认证信息对称解码失败!", var16);
                result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息不合法", "请求头认证信息不合法，请求头认证信息对称解码失败");
            } catch (JSONException var17) {
                this.logger.error("登录信息转换成对象错误:" + subject, var17);
                result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息不合法", "登录信息转换成对象错误");
            } catch (Exception var18) {
                this.logger.error("请求头认证信息不合法，解码失败!", var18);
                result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息不合法", "请求头认证信息不合法，解码失败");
            }

            if (!b) {
                ResponseUtil.responseToPage(response, JSON.toJSONString(result));
                return b;
            } else {
                MDC.put("x-userid", user.getUserid());
                this.logger.info("x-userid:" + user.getUserid());
                user.setData(subject);
                request.setAttribute("_request_key_Authorization", headStr);
                request.setAttribute("Authorization", user);
                if (this.authorhander == null) {
                    return true;
                } else if (!this.authorhander.predoAuthor(user)) {
                    return false;
                } else {
                    Class c = this.authorhander.getUserClass();
                    if (c != null) {
                        Object o = JSON.parseObject(subject, c);
                        request.setAttribute("Authorization_defined", o);
                    }

                    return true;
                }
            }
        } else {
            result = BuildResult.buildOutResult(ResultEnum.ERROR_USER_SESSION_ERROR, "登录信息为空");
            ResponseUtil.responseToPage(response, JSON.toJSONString(result));
            return false;
        }
    }

    public String getPathPatterns() {
        return "/**";
    }
}
