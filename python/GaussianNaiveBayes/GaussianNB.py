# -*- coding: utf-8 -*-
"""
Created on Sat Jun 08 16:11:55 2013

@author: yaohuazhuo
"""

import numpy as np
from sklearn import metrics
import matplotlib.pylab as plt

class NaiveBayes:

        def train(self, X, y):

                n = X.shape[0]
                dim = X.shape[1]

                classes = sorted(set(y))

                n_classes = len(classes)

                mu = np.empty( (n_classes, dim) )
                sg = np.empty( (n_classes, dim) )
                cl_prob = np.empty( n_classes)

                for i, cl in enumerate(classes):
                        mask = (y == cl)

                        Xcl = X[mask, :]

                        mu[i, :] = Xcl.mean(axis = 0)
                        sg[i, :] = Xcl.std(axis = 0)

                        cl_prob[i] = mask.mean()

                self.mu = mu
                self.sg = sg
                self.log_cl_prob = np.log(cl_prob)
                self.classes = classes

        def predict(self, X):

                def log_normal(mu, sg, x):
                        # Gives the log probability of the

                        # Can ignore the constant factors; they come out in the
                        # normalization at the end.
                        return (- np.log(sg) - (mu - x)**2 / (2 * sg**2) ).sum()

                y_predict = np.empty( X.shape[0])

                for i in range(X.shape[0]):
                        cl_prob = np.array([
                                (log_normal(self.mu[cl,:], self.sg[cl,:], X[i,:])
                                 + self.log_cl_prob[cl])
                                for cl in range(self.mu.shape[0])])

                        y_predict[i] = self.classes[np.argmax(cl_prob)]

                return y_predict
                
                
def loadData(filename):
#    X = np.loadtxt(filename, delimiter = ' ')
#    return X[:,0:2], X[:,2].astype(int)
    X = np.loadtxt(filename)
    return X
    
def getErrorRate(yTrue, yResult):
    countError = 0.0
    countTotal = len(yTrue)+0.0
    
    for i in range(len(yTrue)):
        if yTrue[i] != yResult[i]:
            countError += 1
    return countError/countTotal
    
# X_train, y_train = loadData('hw8_p3b_train.csv')
# X_test, y_test = loadData('hw8_p3b_test.csv')
X_train = loadData("train/X_train.txt")
y_train = loadData("train/y_train.txt")
X_test = loadData("test/X_test.txt")
y_test = loadData("test/y_test.txt")

# X_test, y_test,
# Gaussian Naive Bayes


# using sklearn's GaussianNB
print "running GaussianNB from sklearn"
from sklearn.naive_bayes import GaussianNB

GNB = GaussianNB()
GNB.fit(X_train, y_train)

result = GNB.predict(X_train)
print "training error rate using GaussianNB of sklearn: "+str(getErrorRate(y_train, result))
result = GNB.predict(X_test)
print "testing error rate using GaussianNB of sklearn: "+str(getErrorRate(y_test, result))



# using professor's NaiveBayes
print "running NaiveBayes from our professor"
NB = NaiveBayes()
NB.train(X_train, y_train)

result = NB.predict(X_train)
print "training error rate using GaussianNB of professor's version: "+str(getErrorRate(y_train, result))
result = NB.predict(X_test)
print "testing error rate using GaussianNB of professor's version: "+str(getErrorRate(y_test, result))

print "##########################"
print "result len: "+str(len(result))
print "real y len: "+str(len(y_test))