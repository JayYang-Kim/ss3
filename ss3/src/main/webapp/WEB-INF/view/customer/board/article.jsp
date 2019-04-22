<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<div class="alert-info">
    <span style="font-family: Webdings; font-weight: 600;">m</span>
         질문과 답변을 할 수 있는 공간입니다.
</div>

<table style="width: 100%; margin: 20px auto 0px; border-spacing: 0px; border-collapse: collapse;">
<tr height="35" style="border-top: 1px solid #cccccc; border-bottom: 1px solid #cccccc;">
    <td colspan="2" align="center">
	   <c:if test="${dto.depth!=0}">
	       [Re]
	   </c:if>
	   ${dto.subject}
    </td>
</tr>

<tr height="35" style="border-bottom: 1px solid #cccccc;">
    <td width="50%" align="left" style="padding-left: 5px;">
       이름 : ${dto.userName}
    </td>
    <td width="50%" align="right" style="padding-right: 5px;">
        ${dto.created} | 조회 ${dto.hitCount}
    </td>
</tr>

<tr style="border-bottom: 1px solid #cccccc;">
  <td colspan="2" align="left" style="padding: 10px 5px;" valign="top" height="200">
      ${dto.content}
   </td>
</tr>

<tr height="35" style="border-bottom: 1px solid #cccccc;">
    <td colspan="2" align="left" style="padding-left: 5px;">
       이전글 :
        <c:if test="${not empty preReadDto}">
            <a href="javascript:articleBoard('${preReadDto.boardNum}', '${pageNo}');">${preReadDto.subject}</a>
        </c:if>
    </td>
</tr>

<tr height="35" style="border-bottom: 1px solid #cccccc;">
    <td colspan="2" align="left" style="padding-left: 5px;">
       다음글 :
        <c:if test="${not empty nextReadDto}">
            <a href="javascript:articleBoard('${nextReadDto.boardNum}', '${pageNo}');">${nextReadDto.subject}</a>
        </c:if>
    </td>
</tr>
</table>

<table style="width: 100%; margin: 0px auto 20px; border-spacing: 0px;">
<tr height="45">
    <td width="300" align="left">
        <c:if test="${sessionScope.member.userId=='admin'}">
            <button type="button" class="btn" onclick="replyForm('${dto.boardNum}', '${pageNo}');">답변</button>
        </c:if>
        <c:if test="${sessionScope.member.userId==dto.userId}">
            <button type="button" class="btn" onclick="updateForm('${dto.boardNum}', '${pageNo}');">수정</button>
        </c:if>
        <c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
            <button type="button" class="btn" onclick="deleteBoard('${dto.boardNum}', '${pageNo}');">삭제</button>
        </c:if>
    </td>

    <td align="right">
        <button type="button" class="btn" onclick="listPage('${pageNo}')">리스트</button>
    </td>
</tr>
</table>
