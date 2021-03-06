package com.kh.spring.member.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.common.code.ErrorCode;
import com.kh.spring.common.exception.HandlableException;
import com.kh.spring.common.util.file.FileDTO;
import com.kh.spring.common.util.file.FileUtil;
import com.kh.spring.common.validator.ValidateResult;
import com.kh.spring.member.model.dto.Member;
import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.validator.ChangePasswordForm;
import com.kh.spring.member.validator.ChangePasswordValidator;
import com.kh.spring.member.validator.EmailForm;
import com.kh.spring.member.validator.EmailValidator;
import com.kh.spring.member.validator.JoinForm;
import com.kh.spring.member.validator.JoinFormValidator;
import com.kh.spring.member.validator.MypageForm;
import com.kh.spring.member.validator.MypageValidator;
import com.kh.spring.myPage.model.service.MypageService;

@Controller
@RequestMapping("member")
public class MemberController {
   
   Logger logger = LoggerFactory.getLogger(this.getClass());
   
   private MemberService memberService;
   private JoinFormValidator joinFormValidator;
   @Autowired
   private MemberService memberServiceImpl;
   @Autowired
   private GoogleConnectionFactory googleConnectionFactory;
   @Autowired
   RestTemplate http;
   @Autowired
   private OAuth2Parameters googleOAuth2Parameters; 
   @Autowired
   private MypageValidator mypageValidator; 
   @Autowired
   private EmailValidator emailValidator; 
   @Autowired
   private ChangePasswordValidator changePasswordValidator; 
   @Autowired
   ServletContext context; 

   public MemberController(MemberService memberService, JoinFormValidator joinFormValidator) {
      super();
      this.memberService = memberService;
      this.joinFormValidator = joinFormValidator;
   }
   
   
 
   @InitBinder(value = "joinForm") //model??? ?????? ??? ???????????? joinForm??? ????????? ?????? ?????? initBinder ????????? ??????
   public void initBinder(WebDataBinder webDataBinder) {
      webDataBinder.addValidators(joinFormValidator);
   }

   @InitBinder(value = "mypageForm")
	public void initBinderMypage(WebDataBinder webDataBinder) {
	   webDataBinder.addValidators(mypageValidator);
	}
   
   @InitBinder(value = "emailForm")
	public void initBinderSearchPassword(WebDataBinder webDataBinder) {
	   webDataBinder.addValidators(emailValidator);
	}
   
   @InitBinder(value = "changePasswordForm")
   public void initBinderchangePassword(WebDataBinder webDataBinder) {
	   webDataBinder.addValidators(changePasswordValidator);
	}
  
   
   @GetMapping("join")
   public void joinForm(Model model) {
      model.addAttribute(new JoinForm()).addAttribute("error",new ValidateResult().getError());
   }
   
   

   @PostMapping("join")
   public String join(@Validated JoinForm form
         , Errors errors //????????? ????????? ?????? ?????? ?????? ??????
         , Model model
         , HttpSession session
         , RedirectAttributes redirectAttr
         ) {
      
      ValidateResult vr = new ValidateResult();
      model.addAttribute("error",vr.getError());
      
      if(errors.hasErrors()) {
         vr.addError(errors);
         return "member/join";
      }
     
      //token ??????
      String token  = UUID.randomUUID().toString();
      session.setAttribute("persistUser", form);
      session.setAttribute("persistToken", token);
      
      memberService.authenticateByEmail   (form,token);
      redirectAttr.addFlashAttribute("message", "???????????? ?????????????????????.");
      
      return "redirect:/";
   }
   
   

   @GetMapping("join-impl/{token}")
   public String joinImpl(@PathVariable String token
                     ,@SessionAttribute(value = "persistToken", required = false) String persistToken
                     ,@SessionAttribute(value = "persistUser", required = false) JoinForm form
                     ,HttpSession session
                     ,RedirectAttributes redirectAttrs) {
      System.out.println(form);
      if(!token.equals(persistToken)) {
         throw new HandlableException(ErrorCode.AUTHENTICATION_FAILED_ERROR);
      }
      
      if(form.getSocialId() != null) {
    	  memberService.insertSocialMember(form);
    	  return "redirect:/member/login";
      }
      
      
      memberService.insertMember(form);
      redirectAttrs.addFlashAttribute("message", "??????????????? ???????????????. ????????? ????????????");
      session.removeAttribute("persistToken");
      session.removeAttribute("persistUser");
      
      return "redirect:/member/login";
   }
   
  
   @GetMapping("email-check")
   @ResponseBody
   public String emailCheck(String email) {
      Member member = memberService.selectMemberByEmail(email);
      
      if(member == null) {
         return "available";
      }else {
         return "disable";
      }
   }
   
   @PostMapping("join-json")
   public String joinWithJson(@RequestBody Member member) {
      logger.debug(member.toString());
      return "index";
   }

   
   @GetMapping("login")
   public void login() {}; 
   
   @PostMapping("login")
   public String loginImpl(Model model, Member member, HttpSession session, RedirectAttributes redirctAttr) {
      

      System.out.println(member.toString());
      
      Member certifiedUser = memberServiceImpl.selectMemberByEmailAndPassword(member);
      
      
      if(certifiedUser == null) {
         redirctAttr.addFlashAttribute("message","???????????? ??????????????? ???????????? ????????????.");
         return "redirect:/member/login";
      }
      
      session.setAttribute("authentication", certifiedUser);
      logger.debug("?????? ???????????!!!"  + certifiedUser.toString());
      return "redirect:/project/project-list";
      
      
   }

   
//   ????????????
   @GetMapping("logout.do")
   public String logout (HttpServletRequest request) throws Exception{
      logger.info("logout????????? ??????");
      
      HttpSession session = request.getSession();
      session.invalidate();

      
      return "redirect:/member/login";
      
   }
   
 

   
 // ??????
 //????????? ???????????? ???????????? ????????????
 @RequestMapping(value="google_login")
 public String initLogin(Model model, HttpSession session ) throws Exception {

    /* ??????code ?????? */
    OAuth2Operations oauthOperations = googleConnectionFactory.getOAuthOperations();

   /* ?????????????????? ?????? url?????? */
    String url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, googleOAuth2Parameters);

    model.addAttribute("google_url", url);

    System.out.println("???????????? ????????? : " + googleOAuth2Parameters);
    
    /* ????????? ?????? URL??? Model??? ????????? ?????? */
    return "redirect: " + url;
    
    

 }
 
 
 // ?????? Callback?????? ?????????
 @RequestMapping("oauth2callback")
 public String googleCallback(Member member,RedirectAttributes redirectAttr, @RequestParam String code, HttpSession session) throws Exception {

   //?????????????????????, ?????? ?????? 
   System.out.println("Google login success");
   System.out.println("code: "+ code);

   
   //////////////////////////////////////////
// Access Token ??????

   Map<String, String> userJson= memberService.GoogleCallback(code);
   
// ??????????????? ??????
   System.out.println("map?????? ?????????" + userJson);
   String googleId = userJson.get("id");
   String googleName = userJson.get("name");
   System.out.println("googleId : " + googleId);
   System.out.println("googleName : " + googleName);
   session.setAttribute("googleId", googleId);

   
   redirectAttr.addFlashAttribute("name", googleName);
   //////////////////////////////////////////
   Member GoogleUser = memberServiceImpl.selectGoogleId(googleId);
   
   System.out.println("GoogleUser ???????????? ????????? ????????? : "+GoogleUser);
   
   System.out.println("????????? googleId : " + googleId);
   System.out.println("????????? googleName : " + googleName);
   
      if(GoogleUser == null) {
         System.out.println("???????????? ???????????? ????????? ????????????????????? ");
         return "redirect:/member/social-join";
      }
   
      session.setAttribute("authentication", GoogleUser);
      
   //id??? ???????????? ??????????????? ??????.
   return "redirect:/project/project-list";
 }

 
// ?????????????????????
 @GetMapping("social-join")
 public void socialJoinForm(Model model) {
    model.addAttribute(new JoinForm()).addAttribute("error",new ValidateResult().getError());
 }; 
 
 @PostMapping("social-join")
 public String socialJoin(Member member, @Validated JoinForm form, Errors errors, Model model, HttpSession session, RedirectAttributes redirectAttr)  { ValidateResult vr = new ValidateResult();
         model.addAttribute("error",vr.getError());
         
         if(errors.hasErrors()) {
            vr.addError(errors);
            return "member/social-join";
         }
        
         //token ??????
         String token  = UUID.randomUUID().toString();
         
      if(session.getAttribute("kakaoId") != null) {
         form.setSocialId((String)session.getAttribute("kakaoId"));
      }else {
         form.setSocialId((String)session.getAttribute("googleId"));
      }
         session.setAttribute("persistToken", token);
         session.setAttribute("persistUser", form);
         memberService.authenticateByEmail(form,token);
         redirectAttr.addFlashAttribute("message", "???????????? ?????????????????????.");
         
         return "redirect:/member/login";
    
    
//    memberService.insertSocialMember(member);
//    System.out.println(member);
    
   
     }
 
 // ????????? ?????????
 @RequestMapping(value = "kakao_login")
 public String kakaoLogin() {
    StringBuffer loginUrl = new StringBuffer();
    loginUrl.append("https://kauth.kakao.com/oauth/authorize?client_id=");
    loginUrl.append("448c3d7ccd2aea13e02cfe7121e656dc");
    loginUrl.append("&redirect_uri=");
    loginUrl.append("http://localhost:9090/member/kakao_callback");
    loginUrl.append("&response_type=code");

    return "redirect:" + loginUrl.toString();
 }

 @RequestMapping(value ="kakao_callback", method = RequestMethod.GET)
 public String redirectkakao(@RequestParam String code, HttpSession session ,RedirectAttributes redirectAttr ) throws IOException {
 // ???????????? get
    System.out.println("code:"+code);
    
    
    Map<String,Object> kakaoUser = memberService.kakaoCallback(code);
    
    Map<String,String> kakaoProperties =(Map<String,String>) kakaoUser.get("properties");
    String kakaoName = kakaoProperties.get("nickname");
    String kakaoId = String.valueOf(kakaoUser.get("id"));
    System.out.println("kakaoUser"+kakaoUser);
    System.out.println("kakaoId : " + kakaoId);
    System.out.println("kakaoName : " + kakaoName);
    session.setAttribute("kakaoId", kakaoId);
    Member member =  memberService.selectKakaoId(kakaoId);
    session.setAttribute("authentication", member);
    redirectAttr.addFlashAttribute("name", kakaoName);
    
    if(member == null) {
        System.out.println("???????????? ???????????? ????????? ????????????????????? ");
        return "redirect:/member/social-join";
     }
  

  //id??? ???????????? ??????????????? ??????.
  return "redirect:/project/project-list";
           
 }
 
 
 @RequestMapping("kakao_logout")
 public String kakaoLogout() {
 
    StringBuffer logoutUrl = new StringBuffer();
    logoutUrl.append("https://kauth.kakao.com/oauth/logout?client_id=");
    logoutUrl.append("448c3d7ccd2aea13e02cfe7121e656dc");
    logoutUrl.append("&logout_redirect_uri=");
    logoutUrl.append("http://localhost:9090");

   
       System.out.println("????????????????????????.");
       System.out.println("????????? " + logoutUrl);
       return "redirect:/";
 }

 	//**********Mypage Code***********
 	@GetMapping("mypage") 
	public String mypage(HttpSession session, Model model) {
		
		Member member = (Member) session.getAttribute("authentication");
		logger.debug(member.toString());
		//????????? ?????? ??????
		FileDTO fileDTO = memberService.selectProfileImgFilebyMemberIdx(member);
		
		session.setAttribute("authentication", member);
		
		if(fileDTO == null) {
			System.out.println("1. fileDTO ?????? ????????????");
			session.setAttribute("profileImg", "person.png");
		} else {
			session.setAttribute("profileImg", fileDTO.getSavePath()+fileDTO.getRenameFileName());
		}
		
		// Model??? error ?????? ?????? 
		model.addAttribute(new MypageForm()).addAttribute("error",new ValidateResult().getError());
		return "member/mypage"; 
	}
 
 	@PostMapping("profileColor")
	@ResponseBody 
	public String changeProfileColor(@Validated MypageForm mypageForm
			,Errors errors 
			,HttpSession session) {
		
		try {
			//1) ??? Validate ??????
			if(errors.hasErrors()) {
				return "failed"; 
			}
			//2) ??? ??????, SESSION_Member??? ?????? DB??????, ?????? SESSION update 
			String profileColor = "#" + mypageForm.getProfileColor();
			Member tempMember = (Member) session.getAttribute("authentication");
			tempMember.setProfileColor(profileColor); 
			
			int res = memberService.updateMypageMemberByProfileColor(tempMember);
			session.setAttribute("authentication", tempMember);
		} catch(Exception e) {
			e.printStackTrace();
			return "failed";
		}
			//3) color??? ?????? 
			return "#" + mypageForm.getProfileColor(); 
	}
	
	@PostMapping("profileImg")
	@ResponseBody
	public String changeProfileImg(@RequestParam List<MultipartFile> files
			,HttpSession session) {
		
		//1) ?????? ?????? ??? DB?????? 
		FileUtil fileUtil = new FileUtil(); 
		FileDTO fileUploaded = fileUtil.fileUpload(files.get(0));
	
		try {
			Member member = (Member) session.getAttribute("authentication");
			System.out.println(member.getUserIdx());
			int res = memberService.insertMemberProfileImg(fileUploaded, member.getUserIdx()); 
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		//2) profileImg??? join??? ?????? ??????, session ???????????? ?????????
		logger.debug(fileUploaded.getSavePath());
		logger.debug(fileUploaded.getRenameFileName());
		return fileUploaded.getSavePath() +fileUploaded.getRenameFileName(); 
	}
	
	@PostMapping(value= "profileNickname", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String changeProfileNickname(@Validated MypageForm mypageForm
			,Errors errors
			,HttpSession session) {
		
		try {
			//1) ??? validate ?????? 
			if(errors.hasErrors()) {
				return "failed";
			}
			//2) ????????? ??????, SESSION_Member??? ?????? DB??????, ?????? SESSION update
			String nickname = mypageForm.getNickname();
			Member member = (Member) session.getAttribute("authentication"); 
			member.setNickname(nickname);
			
			int res = memberService.updateMypageMemberByNickname(member);
			session.setAttribute("authentication", member);
		} catch(Exception e) {
			e.printStackTrace(); 
			return "failed";
		}
			//3) nickname ?????? 
			return mypageForm.getNickname(); 
	}
	
	@PostMapping(value= "profileGitRepo", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String changeProfileGitRepo(@Validated MypageForm mypageForm
			,Errors errors
			,HttpSession session) {
		
		try {
			//1) ??? validate ?????? 
			if(errors.hasErrors()) {
				return "failed";
			} 
			Member member = (Member) session.getAttribute("authentication"); 
			member.setGit(mypageForm.getGit());
			
			
			//2) git ??????, SESSION_Member??? ?????? DB??????, ?????? SESSION update
			int res = memberService.updateMypageMemberByGit(member); 
			session.setAttribute("authentication", member);
		} catch(Exception e) {
			e.printStackTrace(); 
			return "failed"; 
		}
			//3) git ?????? 
			return mypageForm.getGit(); 
	}
	
	@PostMapping("profilePassword")
	@ResponseBody
	public String changeProfilePassword(@Validated MypageForm mypageForm
			,Errors errors
			,HttpSession session) {
		try {
			//1) ??? validate ?????? 
			if(errors.hasErrors()) {
				return "failed";
			} 
			
			// ????????? ?????????.  
			Member member = (Member) session.getAttribute("authentication");
			// ????????? ????????? ( service????????? password ???????????? ?????? ) 
			String password = mypageForm.getPassword(); 
			member.setPassword(password);
			// ????????? ???????????? ???????????? ????????? 
			Member resMember = memberService.updateMypageMemberByPassword(member); 
			member.setPassword(resMember.getPassword());
			
			session.setAttribute("authentication", member);
		} catch(Exception e) {
			e.printStackTrace(); 
			return "failed"; 
		}
			//3) ?????? ??????   
			return "success";  
	}
	
	
	@PostMapping("isleave")
	@ResponseBody
	public String changeMemberIsleave(HttpSession session) {
			
		//1. session?????? member??????, isLeave ?????? 
		try {
			Member member = (Member) session.getAttribute("authentication"); 
			int res = memberService.memberIsleave(member); 
			session.removeAttribute("authentication");
		} catch(Exception e) {
			e.printStackTrace();
			return "failed"; 
		}
		//2. return "success"
		return "success"; 
	}
 
 	
 	//*********?????????, ???????????? ?????? ?????????*********
	// 1. searchPassword??? forwarding 
 	@GetMapping("searchPassword")
 	public void searchPassword() {}
 	
 	// 2. searchPassword?????? ????????? ??? ??????, ?????? 
 	@PostMapping("searchPassword") 
 	public String searchMemberPassword(@Validated EmailForm emailForm
 			,Errors errors 
 			,HttpSession session
 			,RedirectAttributes redirectAttr 
 			) {
 		
 		try {
 			if(errors.hasErrors()) {
 	 			redirectAttr.addFlashAttribute("message", "????????? ????????? ????????????. ?????? ???????????????");
 	 			return "redirect:/member/searchPassword"; 
 	 		}
 			Member member = memberService.selectMemberByEmail(emailForm.getEmail());  
 	 		
 	 		if(member == null) {
 	 			redirectAttr.addFlashAttribute("message", "???????????? ?????? ??????????????????. ?????? ???????????????");
 	 			return "redirect:/member/searchPassword"; 
 	 		}

 	 		String token  = UUID.randomUUID().toString();
 	 	    Date date = new Date(); 
 	 		
 	 		session.setAttribute("persistToken", token); 
 	 		session.setAttribute("emailSendMember", member);
 	 		session.setAttribute("emailSendTime", date.getTime());
 	 	    memberService.sendPasswordChangeURLByEmail(member, token, emailForm.getProzSendDate());
 	 		 
 	 		
 		} catch(Exception e) {
 			e.printStackTrace();
 			return "redirect:/member/searchPassword";
 		}

 		redirectAttr.addFlashAttribute("message", "???????????? ?????? ?????? ????????? ?????????????????????.");
 		return "redirect:/";
 	}

 	//3. ????????? email?????? ?????? URL ??? ???????????? ?????? 
 	@GetMapping("change-password/{token}")
    public String changePassword(@PathVariable String token
    				  ,@SessionAttribute(value = "persistToken", required = false) String persistToken
                      ,RedirectAttributes redirectAttr
    				  ,HttpSession session
                      ) {
 		if(!token.equals(persistToken)) {
 			redirectAttr.addFlashAttribute("message", "????????? ??????????????????. ???????????? ?????? ???????????? ?????? ???????????????.");
	 		return "redirect:/"; 
 	    }

       return "redirect:/member/changePassword";
    }
 	
 	//4. Email ??????????????? ??????????????? ???????????? ???????????? ?????? 
 	@GetMapping("changePassword")
 	public String changePassword(RedirectAttributes redirectAttr
 			,HttpSession session) {
 		
 		long second = (long) session.getAttribute("emailSendTime")/1000;
 		logger.debug("1. ???????????? ?????? ????????? : " + second);
 		Date date = new Date(); 
 		long currentSecond = date.getTime()/1000;
 		logger.debug("2. ?????? ????????? : " + currentSecond);
 		long passedTime = currentSecond - second;
 		logger.debug("3. ?????? ????????? :  " + passedTime + "??? ?????????.");
 		
 		// ??????????????? ?????? index??? redirect 
 		if(passedTime > 300) {
 			redirectAttr.addFlashAttribute("message", "?????? ?????? ?????? 5?????? ???????????????. ???????????? ?????? ???????????? ?????? ???????????????.");
 			return "redirect:/"; 
 		}
 		
 		// ?????? ?????? ?????? ???????????? changePassword ???????????? forward 
 		return "member/changePassword"; 

 	}
 	
 	//5. changePassword.jsp?????? ????????? ?????? DB??? ?????? 
 	@PostMapping("changePassword")
 	public String passwordChange(@Validated ChangePasswordForm changePasswordForm
 			,Errors errors
 			,HttpSession session
 			,RedirectAttributes redirectAttr) {
 		
 		try {
 			if(errors.hasErrors()) {
 	 			redirectAttr.addFlashAttribute("message", "????????? ???????????? ????????????. ?????? ???????????????");
 	 			return "redirect:/member/changePassword"; 
 	 		}
 			
 			Member member = (Member) session.getAttribute("emailSendMember"); 
 			member.setPassword(changePasswordForm.getPassword());
 			// ?????? protect ?????? ??? update, ?????? ???????????? ?????? 
 			Member changedMember = memberService.updateMypageMemberByPassword(member);  
 		} catch(Exception e) {
 			e.printStackTrace();
 			redirectAttr.addFlashAttribute("message", "???????????? ?????? ???????????? ?????????????????????. ????????? ???????????????");
 			return "redirect:/member/searchPassword";
 		}

 		redirectAttr.addFlashAttribute("message", "???????????? ????????? ?????????????????????.");
 		return "redirect:/member/login";
 		
 	}
 	
 }
   
   
   


