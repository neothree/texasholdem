package com.texasthree.dao;


import com.alibaba.druid.pool.DruidDataSource;
import com.texasthree.utility.utlis.Base64;
import com.texasthree.utility.utlis.RSAUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "release"})
public class DruidDataConfig {

    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK/tXtIqCScQCOhAOtoVcexsDf2VgYz/MsbOheDzaVWatjjrVEs/ZpIGzHJv1dcr7/jAjHZ1/sJgjxJU/UMgCwYmc73CXWEM/6JfJKnCiO88D8jc6sr1MGYdWX6yGu+jBg/Uf61Ovdr+2ni5v9tuq85seuIZOWitY4K0dNK8/7EHAgMBAAECgYBv4Vch9JWpf+sKOH603lp67iTflcqzrj/Oatx9OI9OX3tvK0mKKSHD0AsI49JsaobL/TfWxidj2iBimiYOIDyhd9/VkjXSoHpRRlfN/ekfhbx20DxD/eOLEBBMbK4rHTxl0aFZapzx7Ga4av3u85vLKPL5qI6CmSRESi0n1j9WIQJBAOXC6eTuJmWKF6TdvPgEVVDrldYtfYfjHPfk6jIo3oDzszVHvci2DJ+js2ylpqRkeyRUu+EGElfTZbAOfyE1jQkCQQDEBJzEnGUIzAcFDzZEctp4GH0ZOKOXMghSlLOS7xuKtydt4EpbRxTEXKOnv6sq/bxJWiJdclNsxo/8kMUfWeGPAkEA3PY1oF/T92T11i0VleP89MSMJh07k1q8uj6haDnbumIkX8It54AE5eY2IO+yMnkb4FXJFeCT9XitW8KlSpfImQJAM0eln4XupJuDpp4xKz0EzHBTodqAaZiMNtZyGBl3khWSOht4OGLjGu/FXKg25ltU+7eZz+qNyqIznvZcq3P/HwJAG53bkiwJkc3smABo/NXcmeynzNzHA19tyAAeFTwnsv/m8hA/uMta93C4F7IFCX3KlM75ni+GNnAeHxnp7Ff/rw==";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCv7V7SKgknEAjoQDraFXHsbA39lYGM/zLGzoXg82lVmrY461RLP2aSBsxyb9XXK+/4wIx2df7CYI8SVP1DIAsGJnO9wl1hDP+iXySpwojvPA/I3OrK9TBmHVl+shrvowYP1H+tTr3a/tp4ub/bbqvObHriGTlorWOCtHTSvP+xBwIDAQAB";

    private String driver;

    private String url;

    private String username = "";

    private String password = "";
    /**
     * 初始化连接大小
     */
    private Integer initialSize = 10;
    /**
     * 连接池最小空闲
     */
    private Integer minIdle = 5;
    /**
     * 连接池最大数量
     */
    private Integer maxActive = 100;
    /**
     * 获取连接最大等待时间
     */
    private Integer maxWait = 60000;

    @Primary
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "clone")
    public DruidDataSource druidDataSource() throws Exception {
        var dataSource = new DruidDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:test;IGNORECASE=TRUE;database_to_upper=false;");
        dataSource.setUsername(new String(RSAUtils.decryptByPublicKey(Base64.decode(username), PUBLIC_KEY)));
        dataSource.setPassword(new String(RSAUtils.decryptByPublicKey(Base64.decode(password), PUBLIC_KEY)));

        //初始化时建立物理连接的个数，缺省值为0
        dataSource.setInitialSize(initialSize);
        //最小连接池数量
        dataSource.setMinIdle(minIdle);
        //最大连接池数量，缺省值为8
        dataSource.setMaxActive(maxActive);
        //获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁
        dataSource.setMaxWait(maxWait);
        return dataSource;
    }

    public static void encode(String username, String password) throws Exception {
        System.out.println(Base64.encode(RSAUtils.encryptByPrivateKey(username.getBytes(), PRIVATE_KEY)));
        System.out.println(Base64.encode(RSAUtils.encryptByPrivateKey(password.getBytes(), PRIVATE_KEY)));
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }
}
