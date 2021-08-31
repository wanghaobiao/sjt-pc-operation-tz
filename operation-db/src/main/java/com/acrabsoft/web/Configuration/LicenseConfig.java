package com.acrabsoft.web.Configuration;

import org.acrabsoft.license.LicensePojo;
import org.acrabsoft.license.VerifyLicense;
import org.acrabsoft.license3j.VerifyLicense3j;
import org.acrabsoft.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import zlicense.verify.VerifyLicenseZ;

@Configuration
@ConfigurationProperties(
        prefix = "config.license"
)
public class LicenseConfig {
    private String type;
    private String publicalias;
    private String storepwd;
    private String subject;
    private String licPath;
    private String pubPath;
    private boolean isExpire = false;
    private final String license3j = "license3j";
    private VerifyLicense verifyLicense;

    public LicenseConfig() {
    }

    private void verify(boolean b) {
        try {
            //this.verifyLicense.verify();
            this.isExpire = false;
        } catch (Exception var3) {
            this.isExpire = b;
            this.printOut(var3);
        }

    }

    @Bean
    public VerifyLicense CreateLicense() {
        LicensePojo p = new LicensePojo(this.publicalias, this.storepwd, this.subject, this.licPath, this.pubPath);
        if (StringUtils.isNotEmpty(this.type) && "license3j".equals(this.type)) {
            VerifyLicense vLicense = new VerifyLicense3j();
            this.verifyLicense = vLicense;
        } else {
            VerifyLicense vLicense = new VerifyLicenseZ();
            this.verifyLicense = vLicense;
        }

        this.verifyLicense.setParam(p);
        this.verify(false);
        return this.verifyLicense;
    }

    @Scheduled(
            cron = "0 0 6 * * ?"
    )
    public void executeVerifyLicenseTask() {
        this.verify(true);
        Thread current = Thread.currentThread();
        LogUtil.info("LicenseConfig.executeVerifyLicenseTask 定时任务:" + current.getId() + ",name:" + current.getName());
    }

    public void printOut(Exception er) {
        LogUtil.error("license文件校验失败，请核对当前license是否合法！否则将影响正常使用", er);
    }

    public String getPublicalias() {
        return this.publicalias;
    }

    public void setPublicalias(String publicalias) {
        this.publicalias = publicalias;
    }

    public String getStorepwd() {
        return this.storepwd;
    }

    public void setStorepwd(String storepwd) {
        this.storepwd = storepwd;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLicPath() {
        return this.licPath;
    }

    public void setLicPath(String licPath) {
        this.licPath = licPath;
    }

    public String getPubPath() {
        return this.pubPath;
    }

    public void setPubPath(String pubPath) {
        this.pubPath = pubPath;
    }

    public boolean isExpire() {
        return this.isExpire;
    }

    public void setExpire(boolean isExpire) {
        this.isExpire = isExpire;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}