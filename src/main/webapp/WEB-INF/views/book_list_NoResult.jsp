<%@page import="com.exam.user.UserTO"%>
<%@page import="com.exam.paging.pagingTO"%>
<%@page import="com.exam.booklist.BookTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <%
 	// 현재 세션 상태를 체크한다
 	UserTO userInfo = null;
	if(session.getAttribute("userInfo") != null) {
		userInfo = (UserTO)session.getAttribute("userInfo");
		//System.out.println(session.getAttribute("userInfo"));
	}
 	
	//ArrayList<BookTO> booklists = (ArrayList)request.getAttribute("booklist");
    pagingTO pagelistTO = (pagingTO)request.getAttribute("paginglist");
    ArrayList<BookTO> booklists = pagelistTO.getBookList();
    int cpage = 1;
    if(pagelistTO.getCpage()!= 0){
    	cpage = pagelistTO.getCpage();
    }
    String bookname = "";
    StringBuffer SearchResult = new StringBuffer();
    if (request.getAttribute("bookname") != null){
    	bookname = (String)request.getAttribute("bookname");
    	SearchResult.append("<div ><h1><span id='result'>"+bookname+"</span> 으로 검색한 결과</h1></div>");
    } else {
    	SearchResult.append("<div><h1>도서 리스트</h1></div>");
    }
    
    StringBuffer bookHTML = new StringBuffer();
		
			bookHTML.append("<div>");
			bookHTML.append("<table witdh=100% align='center'><tr><td>");
			bookHTML.append("<img  src='./images/no_result.jpg' />");
			bookHTML.append("</td></tr></table>");
			bookHTML.append("</div>");
	
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>책갈피</title>
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1" crossorigin="anonymous">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/js/bootstrap.bundle.min.js" integrity="sha384-ygbV9kiqUc6oa4msXn9868pTtWMgiQaeYH7/t7LECLbyPA2x65Kgf80OJFdroafW" crossorigin="anonymous"></script>

<!-- sidebar -->
<link rel="stylesheet" type="text/css" href="./css/sidebar.css">
<script type="text/javascript" src="./js/sidebar.js"></script>

<!-- 글쓰기 Summernote -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.js"></script>


<style>
.button1{
	float: right;
	margin-right: 0px;
	width: 30px;
	font-size: 20px;

}
.button2{
	float: right;
	margin-right: 30px;
	width: 30px;
	font-size: 20px;
}
.button3{
	float: right;
	width: 30px;
	font-size: 20px;
}
#list {
  border: 1px solid black;
  padding: 15px;
  width:100%;
}
#innerlist {
  border: 1px solid black;
  padding: 15px;
  width:100%;
}
table {
  border-spacing: 15px;
  padding : "10";
}
#result {
	 font-style: italic;
	 color:blue;
}

.opener { display:none; }
.opener:checked ~ .nav_item { display:block; }
</style>
<script type="text/javascript">
	$(document).ready(function(){
		$("#write_button").on('click', function(){
			<%if(userInfo!=null){%>
				$("#write-modal").modal("show");
				$('.write-content').load("./write.do");
			<%}else{%>
				var comfirm_login = confirm("로그인이 필요한 서비스입니다. \n'확인'버튼을 클릭 시, 로그인 창으로 이동합니다.");
				if(comfirm_login==true){
					location.href="./login.do";
				}
			<%}%>	        	
		});
	});
</script>

</head>
<body>

<div id="mySidebar" class="sidebar">
	<div class="sidebar-header">
		<a><h3>당신의 책갈피</h3></a>
	</div>

	<%if (userInfo != null) {%>
		<div class="sidebar-userprofile">
			<div class="sidebar-user_img" align="center" style="padding-top: 10px; padding-bottom: 10px;">
				<img src="./profile/<%=userInfo.getProfile_filename() %>" border="0" width=80px height=80px style="border-radius: 50%;"/>
			</div>	
			<div  align="center" style="color:gray; font-size:18px;"><%=userInfo.getNickname()%>님이</div>
			<div  align="center" style="color:gray; font-size:18px;">로그인 중 입니다.</div>
			<br/>
		</div>
	<%} else {%>
		<p>로그인해주세요.</p>
	<%}%>
	<a href="./home.do">Home</a>
		<%if(userInfo != null){
		if(userInfo.getId().equals("testadmin1")) {%>
			<a href="./admin.do">Admin Page</a>
		<%} else{ %>
			<a href="./mypage.do?useq=<%=userInfo.getSeq()%>" >My Page</a>
		<%}
	}%>
	<a href="./list.do">모든 게시글 보기</a>
	<a href="./book_list.do">책 구경하기</a>
	
	<div style="padding:8px; position:absolute; bottom:2%; width:100%">
		<button style="width:100%" id="write_button" type="button" class="btn btn-outline-light">글쓰기</button>
	</div>
</div>

<div id="main">
	<div id="header">
		<div>
			<table>
				<tr>
					<td width=5%><span>
						<button class="sidebar-btn" onclick="sidebarCollapse()">
							<span><i class="fa fa-bars" aria-hidden="true"></i></span>
						</button>
					</span>
					</td>
					<td width=5%><span><a class="navbar-brand" href="./home.do"> <img src="./images/logo.png" alt="logo" style="width: 200px; height:50px; "></a></span></td>
					<% if(userInfo == null){ %>
						<td width=75% ><span><a class="button1" href="./login.do" id="start-button" style="color: black;">START</a></span></td>
					<% }else{ %>
						<td width=75% ><span><a class="button2" href="./logout_ok.do" id="logout-button" style="color: black;">LOGOUT</a></span></td>
					<% } %>
					<td width=5%><span><a class="button3" href="./search.do" style="color: black;"><i class="fa fa-search" aria-hidden="true"></i></a></span></td>
				</tr>
			</table>		
		</div>
    </div>
    
    <div id="content" width=100%>
        <%=SearchResult %>
        <div width=100%>
        	<table >
        	
        	<tr><td width = 100% height="40"> <span id="result">검색 결과가 없습니다.</span></td></tr>
        		<tr>
        			<td height="50" width="500"></td>
        			<td>
        				<table>
        				<form action="./book_list_search.do" type="get" align = "right">
        				<tr>
	        				<td width="50">
	        					<select name="search">
								    <option value="제목" selected="selected">제목 검색</option>
								    <option value="작가">작가 명 검색</option>
								</select>
							</td>
        					<td>
        						<input type="text" name = "bookname" width="50"/>
       						</td>
        					<td>
        						<input type="submit" value="검색" width="100"/>
       						</td>
        				</tr>
        				</form>
        				</table>
        			</td>
        		</tr>
        	</table>
        </div>
     </div>
     <div>
   		<%= bookHTML %>
     </div>
     <div><input type='button' onclick='history.back()' value='뒤로 가기'></div>
     <br><br>
     <div align="center">
     </div>
     <br><br>
</div>
<!-- 모달창 정보 -->
<div id="write-modal" class="modal fade" tabindex="-1" role="dialog">
	<div class="modal-dialog modal-xl modal-dialog-centered">
		<div class="modal-content write-content">
		</div>
	</div>
</div>
</body>
</html>