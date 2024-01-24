In this problem, you will write a short program to convert color declarations in CSS files from hexadecimal to rgb form.

The primary objective is to give you more exposure to hexadecimal as an output format for binary data, in a realistic context. You should try to solve the problem in a way where you do the hexadecimal to decimal conversion with minimal use of library functions.

Test files are provided. You may want to start with more discrete tests like:

```
assert hex_to_rgb('#00ff00') == 'rgb(0, 255, 0)'
```

Eventually you should be able to run something like:

```
diff <(cat advanced.css | python3 convert.py) advanced_expected.css
```

and see that there is no output in the diff.

As usual, you should watch the first section of the video for more context, and possibly more if you get stuck.

Good luck!
