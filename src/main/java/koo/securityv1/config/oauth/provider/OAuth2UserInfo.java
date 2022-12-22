package koo.securityv1.config.oauth.provider;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getEmail();
    String getName(); // 유저의 실제 이름

}
