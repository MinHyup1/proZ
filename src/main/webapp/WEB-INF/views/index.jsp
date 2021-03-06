<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/views/include/head.jsp" %>
<link rel="stylesheet" href="/resources/css/index.css">
<title>proz index</title>
<style type="text/css">


 a:link { color: #fff; text-decoration: none;}
 a:visited { color: #fff; text-decoration: none;}
 a:hover { color: #fff; text-decoration: none;}
 </style>
</head>
<body>

<c:if test="${not empty message}">
	<script type="text/javascript">
		alert("${message}");
	</script>
</c:if>
<div id="wrap">
        
            <header>
                <div class="middleheader-wrap">
                    <div class="leftheader">
                            <div class="project" onclick="location.href='/'">home</div>
                            <div class="templates" onclick="location.href='/project/project-list'">projects</div>
                    </div>
                    <div class="logo"><img src="/resources/img/LOGO0000w.png"></div>
                    
                    <div class="rightheader">
                            
                            <c:set var="loginout" value="${authentication}" />
                        <c:choose>
	                        <c:when test="${empty loginout}">
								 <div class="signup" onclick="location.href='/member/join'">sign-up</div>
							</c:when> 
							<c:when test="${!empty loginout}">
							      <div class="signup" onclick="location.href = '/member/mypage'">mypage</div>
							 </c:when>
                        </c:choose>
                            <div class="login" ><a href="/member/logout.do">
                            <c:set var="loginout" value="${authentication}" />
                        <c:choose>
	                        <c:when test="${empty loginout}">
								 login
							</c:when> 
							<c:when test="${!empty loginout}">
							      logout
							 </c:when>
                        </c:choose>
                            </a></div>
                    
                </div>
            </header>

                <div id="contents">
                <section id="beforelogin">
                    <div class="mainimg"><img src="/resources/img/index.png"></div>
                    <div class="maintext">
                        <div class="textlogo"><img src="/resources/img/logo-white.png"></div>
                        <div class="des1">Proz is used for better project work.</div>
                        <div class="des2"> click the button to join us.</div>
                        <div class="startbtn" >
                        <c:set var="loginout" value="${authentication}" />
                        <c:choose>
	                        <c:when test="${empty loginout}">
								 <div class="signup" onclick="location.href='/member/login'">start now!</div>
							</c:when> 
							<c:when test="${!empty loginout}">
							      <div class="signup" onclick="location.href='/project/project-list'">start now!</div>
							 </c:when>
                        </c:choose>
                        </div>
                    </div>
                </section>

            <footer>
                <div class="footleft">
                    <div class="footdes1">project by DragonBall.</div>
                    <div class="footdes2">Lim Ji-young , Kang Min-hyup ,  Gil Ye-jin , Son Eun-bi , Lee Yoo-song , Choi Yun-ji</div>
                </div>
                <div class="footright">Email_ lucky007proz@gmail.com</div>

            </footer>
        </div>
    </div>


</body>
</html>