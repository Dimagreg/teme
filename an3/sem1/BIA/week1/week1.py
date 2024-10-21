
# Laboratory 1

import numpy as np

#1
x = np.array([[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12], [13, 14, 15, 16], [17, 18, 19, 20]])

print(x)
print(x[2:, 1:])

#2 
def perfect_number(x):
    sum = 0
    for i in range(1, x):
        if x % i == 0:
            sum += i
    return sum == x

print(perfect_number(6)) # 1 + 2 + 3 = 6, True
print(perfect_number(7)) # False

#3
class Text:
    def __init__(self, text):
        text = text.lower()
        text = text.replace(".", "")
        text = text.replace("!", "")
        text = text.replace("?", "")
        text = text.replace(",", "")

        self.fmtText = text

    def strLength(self):
        return len(self.fmtText)
    
text1 = Text("Hello, World!")

print(text1.fmtText)
print(text1.strLength())