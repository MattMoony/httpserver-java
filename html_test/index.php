<!DOCTYPE html>
<html>
    <head>

    </head>
    <body>
        <h1>Hello World!</h1>

        <img src="/cat.png" alt="This is some picture of a cat ... " width="500" height="auto" />
        <video width="500" height="auto" controls autoplay loop>
            <source type="video/mp4" src="/video.mp4" />
            video cannot be displayed ...
        </video>

        <?php
            $str = "Hello World!";
            echo($str);
            echo ( $str );
        ?>

        <p>This is some other text ... </p>

        <?php
            echo("Are you still there?");
        ?>

        <div id="php3">
            <?php
                echo  ("Extreme, test!() asdf           asdf\n,,", "something");
            ?>
        </div>
    </body>
</html>