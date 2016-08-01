<%@ page language="java" isELIgnored="false"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>

<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="<c:url value="/static/css/bootstrap.min.css" />" />
<link rel="stylesheet"
	href="<c:url value="/static/css/bootstrap-responsive.min.css" />" />

<script type="text/javascript"
	src="<c:url value="/static/js/jquery-1.10.2.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/static/js/bootstrap.min.js" />"></script>
</head>

<body>
	<div class="span9">
		<h2 class="text-center">Xác Nhận Thanh Toán</h2>
		<br /> <br />

		<!-- Invoice -->
		<table class="table table-bordered table-striped">
			<thead>
				<tr>
					<th style="text-align: center;" width="15px">STT</th>
					<th style="text-align: center;" width="200px">Tên Hàng Hóa,
						Dịch Vụ</th>
					<th style="text-align: center;" width="75px">Đơn Giá</th>
					<th style="text-align: center;" width="30px">Số Lượng</th>
					<th style="text-align: center;" width="75px">Thành Tiền</th>
				</tr>
			</thead>

			<tbody>
				<c:forEach items="${invoice.items}" varStatus="vs" var="item">
					<tr>
						<td style="text-align: center;"><c:out value="${vs.index+1}" /></td>

						<td><c:out value="${item.name}" /></td>

						<td style="text-align: right;"><span class="currency-amount"><fmt:formatNumber
									value="${item.price}" pattern="#,##0" /></span></td>

						<td style="text-align: center;"><c:out
								value="${item.quantity}" /></td>

						<td style="text-align: right;"><span class="currency-amount"><fmt:formatNumber
									value="${item.amount}" pattern="#,##0" /></span></td>
					</tr>
				</c:forEach>

				<c:if test="${invoice.serviceFee ne 0}">
					<tr class="text-info">
						<td colspan="4" style="text-align: right;">Cộng Tiền</td>
						<td style="text-align: right;"><span class="currency-amount"><fmt:formatNumber
									value="${invoice.amount}" pattern="#,##0" /></span></td>
					</tr>

					<tr class="text-warning">
						<td colspan="4" style="text-align: right;">Phí Dịch Vụ</td>
						<td style="text-align: right;"><span class="currency-amount"><fmt:formatNumber
									value="${invoice.serviceFee}" pattern="#,##0" /></span></td>
					</tr>
				</c:if>


				<tr class="text-success">
					<td colspan="4" style="text-align: right;">Tổng Cộng</td>
					<td style="text-align: right;"><span class="currency-amount"><fmt:formatNumber
								value="${invoice.paidAmount}" pattern="#,##0" /></span></td>
				</tr>

				<c:if test="${installment != 'null'}">
					<tr class="text-warning">
						<td colspan="5" style="text-align: right;"><c:out
								value="${installment}" /></td>
					</tr>
				</c:if>

			</tbody>
		</table>

		<!-- Cancel or Pay -->
		<div style="text-align: center;">
			<button id="btnCancel" class="btn btn-danger"
				onclick="window.location.href='<c:url value="/web/cancel.htm?checkoutId=${checkoutId}"/>'">Hủy
				bỏ</button>

			<button id="btnPay" class="btn btn-info" data-toggle="modal">Thanh
				toán</button>
		</div>

		<!-- OTP Dialog -->
		<div id="otpDialog" class="modal hide fade" tabindex="-1"
			role="dialog" aria-labelledby="otpDialogLabel" aria-hidden="true">

			<div class="modal-header">
				<a href="#" class="close" data-dismiss="modal">&times;</a>
				<h3>Nhập Mã Xác Nhận</h3>
			</div>

			<div class="modal-body">
				<form class="form-inline">
					<input id="txtOtp" class="input-large" type="text" maxlength="10" />
					<button id="btnReissueOtp" class="btn btn-link" type="button">Gửi
						lại mã xác nhận</button>
				</form>
				<div id="txtError" class="alert alert-error hide">
					<p>Mã xác nhận không chính xác!</p>
				</div>
			</div>

			<div class="modal-footer">
				<button class="btn" data-dismiss="modal" aria-hidden="true">Đóng</button>
				<button id="btnConfirm" class="btn btn-primary">Xác nhận</button>
			</div>

		</div>

		<script>
			$('#btnPay').click(function() {
				$('#otpDialog').modal('show');
			});

			$('#otpDialog').on('shown', function() {
				$('#txtOtp').val('');
				$('#txtOtp').focus();
				$('#txtError').hide();
			});

			$('#btnReissueOtp')
					.click(
							function() {
								$
										.ajax({
											url : '<c:url value="/web/otp.htm?checkoutId=${checkoutId}"/>'
										});
							});

			function doPayment() {
				$
						.ajax({
							url : '<c:url value="/web/otp.htm" />',
							type : 'POST',
							data : 'checkoutId=${checkoutId}' + "&otp="
									+ $('#txtOtp').val(),
							success : function(response) {
								$('#txtOtp').val('');

								if (response == 0) {
									$("#txtError").show();
									$('#txtOtp').focus();
								} else if (response == 1) {
									$('#otpDialog').modal('hide');
									window.location.href = '<c:url value="/web/pay.htm?checkoutId=${checkoutId}"/>';
								} else {
									window.location.href = '<c:url value="/web/cancel.htm?checkoutId=${checkoutId}"/>';
								}
							}
						});
			}

			$('#btnConfirm').click(doPayment);

			$('#otpDialog').keypress(function(e) {
				if (e.which == 13) {
					doPayment();
					return false;
				}
			});
		</script>


	</div>

</body>

</html>