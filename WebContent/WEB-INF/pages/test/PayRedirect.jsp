<%
boolean isForm = false;
String url = (String) request.getAttribute("url");
if (url.indexOf("form name=") != -1) isForm = true;
if (isForm) {
%>
<%=url%>
<%}%>

<script language="javascript">

<%if (isForm) {%>
    document.PaymentForm.submit();
<%} else {%>
    window.location.href = "<%=url%>";
<%}%>

</script>


