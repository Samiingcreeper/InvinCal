```
Welcome to InvinCal.

This caculator can evaluate expressions. You may also create functions and constants.

The latest version is InvinCal - Recursive Definition Detection

Inputting Expression:             3 + 4 * factorial(5)

Creating Custom Function:         f(x) --> x * x * x 

Modifying Custom Function:        f(x) --> x   f(x,y,z) --> x * y * z

* Modifying the number of variables will cause other functions/constants calling

  the function to return NaN.

Creating/Modifying Constant so that the definition is the value of the expression: 

              c -> 3 + 4 / 2       c = 5        

              c -> 3 + 4 * f(3)    if f(x) = -x in this moment, c = -9.

Creaing/Modifying Constant so that the defintion is the expression itself: 

              c ->--expression f(2)         if f(x) = x, c = 2. if f(x) = -x, c = -2.
                
* Do not recursively define constants and functions. Your modification will be rejected. 

Function names, function variable names, and constants names can only contain 

a-z A-Z 0-9 and must has at least 1 character. 


Deleting Function/Constant:  f -->--delete     for function

                             c  ->--delete     for constant

* Be careful that if other functions or constants reference to a delete function/constant, 

  they will return NaN when being called.  

```
