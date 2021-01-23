# InvinCal
InvinCal is a calculator with functions, which can be written in Java with high flexibility, or created in runtine by combining the available functions for temporary use (not implemented). Creating custom constants will be available in later versions. I am also planning to add 'programs' to the calculator, just as the programs available in the calculator you use in college or high school. Of course, custom programs can be added with the help of Java.

----------------------------------------------------------------------
2021/1/24 InvinCal - Function No Recursive Definition Detection

You can create or modify a constant by inputting "x -> 5", "x -> combination(2,1)", "x -> f(2,4) + g(3)" etc.
Input "x -> --delete" to delete the constant. (If deleted constants are referenced by calling getValue(), NaN will be returned ).
The program has already created constant "A" - "G" automatically. They are not deletable.

You can create or modify a function by inputting "f(x,y) --> pow(factorial(x),y)", "g(x) --> A + x" etc.
Input "f --> --delete" to delete the function. (If deleted functions are referenced by calling getValue(), NaN will be returned).

If you define a constant with a custom function, and you change the definition of that function, the constant will probably yield a different value. Vice versa.

Never ever recursively define constants and functions. This would cause stack overflow! This program is not going to support recursive definition as well (such as defining a function to generate Fibonacci numbers, this is quite a lot (tons) of work to do but I don't think it's worthy).

Later on, I will try to create a GUI for my program.

![image](https://github.com/Samiingcreeper/InvinCal/blob/main/InvinCal.png)

#Arithmetic_Calculator
#Expression_Calculator
#Function
