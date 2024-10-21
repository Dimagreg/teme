import numpy as np
np.random.seed(42) # to make the outputs stable across runs

X = 2 * np.random.rand(100, 1)
y = 4 + 3 * X + np.random.randn(100, 1)

print(X)
print(y)