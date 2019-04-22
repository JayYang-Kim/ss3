<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<div class="alert-info">
    <span style="font-family: Webdings; font-weight: 600;">m</span>
    1:1 문의 공간 입니다. 문의 결과를 이메일, 핸드폰등으로 확인 가능합니다.
</div>

<table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px; border-collapse: collapse;">

<c:forEach var="vo" items="${list}" varStatus="vs">
<c:if test="${vs.index==0}">
	<tr height="35" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;">
	    <td colspan="2" align="left" style="padding-left: 5px; font-weight: 600;">
		     [문의] ${vo.subject}
	    </td>
	</tr>
	
	<tr height="35" style="border-bottom: 1px solid #cccccc;">
	    <td width="50%" align="left" style="padding-left: 5px;">
	         이름 : ${vo.userName}(${vo.userId})
	    </td>
	    <td width="50%" align="left" style="padding-right: 5px;">
	        문의일자 : ${vo.created}
	    </td>
	</tr>
	
	<tr height="35" style="border-bottom: 1px solid #cccccc;">
	    <td width="50%" align="left" style="padding-left: 5px;">
	         구분 : ${vo.category}
	    </td>
	    <td width="50%" align="left" style="padding-right: 5px;">
	       상태 : ${vo.state=="0"?"처리중":"답변완료"}
	    </td>
	</tr>
	
	<tr height="35" style="border-bottom: 1px solid #cccccc;">
	    <td width="50%" align="left" style="padding-left: 5px;">
	       이메일 : 이메일 답변 ${vo.emailRecv=="1"?"O":"X" }
	           <c:if test="${not empty vo.email}">(${vo.email})</c:if>
	    </td>
	    <td width="50%" align="left" style="padding-right: 5px;">
	       전화번호 : 문자 답변 ${vo.phoneRecv=="1"?"O":"X" }
	           <c:if test="${not empty vo.phone}">(${vo.phone})</c:if>
	    </td>
	</tr>

	<tr style="border-bottom: 1px solid #cccccc;">
	  <td colspan="2" align="left" style="padding: 10px 5px;" valign="top" height="70">
	      ${vo.content}
	   </td>
	</tr>
</c:if>

<c:if test="${vo.type==1}">
	<tr height="35" style="border-bottom: 1px solid #cccccc;">
	    <td colspan="2" align="left" style="padding-left: 5px; font-weight: 600;">
		     [답변] ${vo.subject}
	    </td>
	</tr>
	<tr height="35" style="border-bottom: 1px solid #cccccc;">
	    <td width="50%" align="left" style="padding-left: 5px;">
	         담당자 : ${vo.userName}
	    </td>
	    <td width="50%" align="left" style="padding-right: 5px;">
	        답변일자 : ${vo.created}
	    </td>
	</tr>
	
	<tr style="border-bottom: 1px solid #cccccc;">
	  <td colspan="2" align="left" style="padding: 10px 5px;" valign="top" height="70">
	      <div style="min-height: 50px; ">${vo.content}</div>
	      <c:if test="${sessionScope.member.userId=='admin'}">
	         <div style="margin-top: 5px; margin-bottom: 5px; text-align: right;">
                  <a href="javascript:deleteBoard('${vo.num}', '${pageNo}')">답변삭제</a>
	         </div>
	      </c:if>
	   </td>
	</tr>
</c:if>
</c:forEach>

<tr height="45">
    <td align="left">
       <button onclick="javascript:deleteBoard('${dto.num}', '${pageNo}');" class="btn">문의삭제</button>
	</td>
	<td align="right">
	       <button type="button" class="btn" onclick="listPage('${pageNo}')">리스트</button>
	</td>
</tr>
</table>

<c:if test="${sessionScope.member.userId=='admin'}">
<form name="boardForm" method="post" enctype="multipart/form-data">
<table style='width: 100%; margin: 15px auto 0px; border-spacing: 0px;'>
    <tr height='30'> 
        <td align='left'>
            <span style='font-weight: bold;' >답변 달기 - </span><span> 문의에 대한 답변을 입력 하세요</span>
        </td>
    </tr>
    <tr>
        <td style='padding:5px 5px 0px;'>
            <textarea name='content' class='boxTA' style='width:99%; height: 70px;'></textarea>
        </td>
    </tr>
    <tr>
        <td align='right'>
            <button type='button' class='btn' style='padding:10px 20px;' onclick="sendOk('reply', '${pageNo}');">답변 등록</button>
        </td>
    </tr>
</table>
    <input type="hidden" name="subject" value="${dto.subject}">
    <input type="hidden" name="category" value="${dto.category}">
    <input type="hidden" name="parent" value="${dto.num}">
    <input type="hidden" name="email" value="${dto.email}">
    <input type="hidden" name="emailRecv" value="${dto.emailRecv}">
    <input type="hidden" name="phone" value="${dto.phone}">
    <input type="hidden" name="phoneRecv" value="${dto.phoneRecv}">
</form>
</c:if>