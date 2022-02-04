<!doctype html>
<html class="no-js" lang="">

<head>
    <meta charset="utf-8">
    <title>Password Reset Email</title>
    <meta name="description" content="Password reset Email">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta property="og:title" content="">
    <meta property="og:type" content="">
    <meta property="og:url" content="">
    <meta property="og:image" content="">

    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <link rel="stylesheet" href="${staticRoot}thelistshop.css">
    <!--<link rel="stylesheet" href="../css/thelistshop.css">-->
</head>

<body>

<!-- header -->
<div class="container ls-email-header"><a href="https://www.the-list-shop.com">
        <div class="ls-logo-img"></div>
        <div class="ls-logo-title">the list shop</div>
    </a>
</div>

<!-- spacer -->
<div class="container ">
    <div class=" ls-spacer"></div>
</div>
<!-- content -->
<div class="container">
    <div class="row">
        <div class="col-sm">
            <p>Hello!</p>

            <p>This email is here to help you reset your password, and get back to making lists.</p>
            <p>You can reset your password by clicking on this link:</p>
        </div>
    </div>
    <div class="row justify-content-center">
        <div class="col ls-button-padding">
            <div>

                <div class="col ls-button-padding">
                    <a href="${tokenLink}" class="ls-user-button ">Reset Your Password</a>
                </div>
                <div class="col ls-button-padding">
                    <div>
                    </div>
                    <div class="row">
                        <div class="col-sm">
                            <p>You'll be directed to a page where you can enter a new password.</p>
                            <p>Or, you can copy and paste the following link into your browser: .</p>
                            <p>If you have any questions, you can contact us at test@email.com.</p>
                            <p>See you soon,</p>
                            <p>The List Shop</p>

                        </div>
                    </div>


</body>

</html>
