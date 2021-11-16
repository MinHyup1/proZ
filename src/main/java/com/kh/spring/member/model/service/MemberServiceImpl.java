package com.kh.spring.member.model.service;




import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.spring.common.code.Config;
import com.kh.spring.common.mail.MailSender;
import com.kh.spring.member.model.dto.Member;
import com.kh.spring.member.model.repository.MemberRepository;
import com.kh.spring.member.validator.JoinForm;

import lombok.RequiredArgsConstructor;

import com.kh.spring.member.model.dto.Member;
import com.kh.spring.member.model.repository.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService{

   @Autowired
   private MemberRepository memberRepository;
   
   @Autowired
   private  MailSender mailSender;
   
  @Autowired
   private  RestTemplate http;
   
   @Autowired
   private  PasswordEncoder passwordEncoder;
   
   public Member selectMemberByEmail(String email) {
      return memberRepository.selectMemberByEmail(email);
   }
   
   public void insertMember(JoinForm form) {
      form.setPassword(passwordEncoder.encode(form.getPassword()));
        memberRepository.insertMember(form); }
   
   public void authenticateByEmail(JoinForm form, String token) {
      MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
      body.add("mailTemplate", "join-auth-mail");
      body.add("nickname", form.getNickname());
      body.add("persistToken", token);
      
      //RestTemplate의 기본 Content-type : application/json
      RequestEntity<MultiValueMap<String, String>> request =
            RequestEntity.post(Config.DOMAIN.DESC + "/mail")
            .accept(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body);
      
      String htmlTxt = http.exchange(request, String.class).getBody();
      mailSender.send(form.getEmail(), "회원가입을 축하합니다.", htmlTxt);
   }

   public Member selectMemberByEmailAndPassword(Member member) {
      Member storedMember = memberRepository.selectMemberByEmail(member.getEmail());
      
      if(storedMember != null && passwordEncoder.matches(member.getPassword(), storedMember.getPassword())) {
         return storedMember;
      }
      
      return null;
      }

@Override
public String getReturnAccessToken(String code) {
   // TODO Auto-generated method stub
   return null;
}

@Override
public Map<String, Object> getUserInfo(String kakaoToken) {
   // TODO Auto-generated method stub
   return null;
}




@Override
public Map<String, String> GoogleCallback(String code) throws IOException {
   HttpHeaders headers = new HttpHeaders();
   
   
   headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
   
   MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
   parameters.add("code", code);
   parameters.add("client_id", "949983700709-cv3r1tlbqog8v1eqq4u1nicc9b6p9oif.apps.googleusercontent.com");
   parameters.add("client_secret", "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");
   parameters.add("redirectUri", "http://localhost:9090/member/oauth2callback");
   parameters.add("grant_type", "authorization_code");
   
   HttpEntity<MultiValueMap<String, String>> rest_request = new HttpEntity<>(parameters,headers);
   
   URI uri = URI.create("https://accounts.google.com/o/oauth2/token");
   ResponseEntity<String> rest_response = http.postForEntity(uri, rest_request, String.class);
   String bodys = rest_response.getBody();

   //access키를 포함한 정보 가져오기 성공
   System.out.println(bodys);
   
   //가져온 json 데이터들에서 access_key만 추출하기
   ObjectMapper mapper = new ObjectMapper();
   Map<String, String> jsonMap = mapper.readValue(bodys , Map.class );
   System.out.println("map으로 바꾸기" + jsonMap);
   String token = jsonMap.get("access_token");
   System.out.println("access_token : " + token);
   
   //요청

   String url2 = "https://www.googleapis.com/oauth2/v1/userinfo?access_token={token}";
   
    String json2 = http.getForObject(url2, String.class , token );
    System.out.println(json2);
    
    
    Map<String, String> userJson = mapper.readValue(json2 , Map.class );
   

   
   
   return userJson;
 }


@Override
public Member selectGoogleId(String googleId) {
	System.out.println("serviceimpl구글아이디 타나?" + googleId);
   return memberRepository.selectGoogleId(googleId);
  }

@Override
public void insertSocialMember(JoinForm form) {
   memberRepository.insertSocialMember(form);
   
}







}