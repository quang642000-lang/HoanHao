<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<div class="d-flex justify-content-center mt-4 mb-3">
    <nav aria-label="Page navigation">
        <ul class="pagination pagination-sm shadow-sm rounded-3">
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link text-brand fw-bold" href="${pageContext.request.contextPath}${param.baseUrl}&page=${currentPage - 1}">Trước</a>
            </li>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${currentPage == i ? 'active' : ''}">
                    <a class="page-link ${currentPage == i ? 'bg-brand border-brand text-white' : 'text-dark fw-medium'}" href="${pageContext.request.contextPath}${param.baseUrl}&page=${i}">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link text-brand fw-bold" href="${pageContext.request.contextPath}${param.baseUrl}&page=${currentPage + 1}">Sau</a>
            </li>
        </ul>
    </nav>
</div>