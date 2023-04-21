<!doctype html>
<head>
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Poppins">
    <style>
        table {
            padding: 20px 30px 20px 20px;
        }

        @media only screen and (max-width: 620px) {
            body {
                min-height: 844px;
            }

            a {
                font-size: large;
            }

            table {
                padding: 20px 20px 20px 20px;
            }
        }
    </style>
</head>

<body style="font-family: Poppins;
    background: linear-gradient(to top left, #dddbe000 6%, #dddbe0 232%);
    background-size: 100% auto;
    background-repeat: no-repeat;
    min-height: 1000px;
font-size: large">
<div></div>
<table style="    margin-top: 40px;
    background-color: white;
    min-width: 500px;
    margin-left: auto;
    margin-right: auto;
    border-radius: 20px;
    border-color: #c2c0c5;
    border-style: solid;
    box-shadow: 10px 10px 26px 5px rgb(0 0 0 / 28%);">
    <tr>
        <td><img style="width:75px;margin-top:-40px; margin-left:40px;" src="${staticRoot}/images/lock.png"></td>
    </tr>
    <tr>
        <td>
            <p>Hello!</p>
            <p>This email is here to help you reset your password, and get back to making lists.</p>
            <p>You can reset your password by clicking on this link:</p>
            <div style="background-color:#e67d21; width: 50%;align-content: center;text-align:center;margin-left:auto;
      padding: 10px;margin-right: auto;border-radius: 15px;">
                <a style="text-align:center;color: white; text-decoration: none" href="${tokenLink}">Reset Your
                    Password</a></div>
            <p>You'll be directed to a page where you can enter a new password.</p>
            <p>Or, you can copy and paste the following link into your browser: ${tokenLink}.</p>
            <p>Having trouble? We can help - contact us at ${supportEmail}</p>
            <p>Didn&rsquo;t request a password reset? You can ignore this message.</p>
            <p>See you soon,</p>
            <div style="vertical-align: top">
                <div style="background-image: url(${staticRoot}/images/smallorangelogo.png);
                        width: 90px;
                        height: 90px;
                        background-repeat: no-repeat;
                        display: inline-block;
                        text-align: justify;
                        vertical-align: center;box-sizing: border-box;"></div>
                <div style="    padding: 5px 10px 10px 10px;
    display: inline-block;
    font-weight: 400;
    line-height: 30px;
    font-size: 20px;
    letter-spacing: 1px;
    color: #e67d21;
    text-transform: uppercase;
    vertical-align: bottom;
    margin-left: -30px;
    margin-bottom: 15px;
    box-sizing: border-box;">the list shop
                </div>

            </div>

        </td>
    </tr>
</table>

</body>
