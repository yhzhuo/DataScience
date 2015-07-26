# Author: ZHUO, Yaohua

# pre: x is the matrix; y is the the truth values, lamada is the lasso coefficient
# post: return the w vector without lasso optimization
wBeforeOptimize <- function(x, y, lamada) {
  return (solve(t(x)%*%x + lamada * diag(ncol(x)))%*%t(x)%*%y)
}

# pre: w is the the unoptimized w
optimize <- function(w, X, Y, lamada) {
  wPrev <- w
  w0 <- lm(V1 ~ ., data = training)$coeff[1]
  while(1 == 1) {
    for(j in 1:ncol(X)) {
      a.j <- 2*(sum(X[,j]^2))
      # c.j <- 2*(sum(X[,j] %*% (Y - X%*%as.matrix(w) + w[j]*X[,j])))
      c.j <- 2*(sum(X[,j] %*% (Y - (w0 + X%*%as.matrix(w)) + w[j]*X[,j])))
      # Y - (w0 + X%*%as.matrix(w))
      if(c.j < -lamada) {
        w[j] <- (c.j + lamada)/a.j
      } else if(c.j > lamada) {
        w[j] <- (c.j - lamada)/a.j
      } else {
        w[j] <- 0
      }
      w0 <- sum(Y - X%*%w)/dim(Y)[1]
      # aj = 2*sum((x[,j])^2)
      # cj = 2*sum() # x[,j]%*%(y )
    }
    # cat(w, "\n")
    # cat("this is w0", w0, "\n")
    # if(sum((wPrev - w)^2) < 10^-6) {   #may have problems
    if(max(abs(wPrev - w)) < 10^-6) {
      return (w)
    }
    # use new w to calcualte the intercept
    
    # set
    wPrev <- w
  }
  return (1:12,nrow=3)
}



#pre: w: optimized w; X: value sets matrix; Y: truth value
#post: return the training error
trainingError <- function(w, X, Y, w0) {
  return (sum((Y - X%*%w - w0)^2)/dim(Y)[1])
}

#pre: w: optimized w; X: value sets matrix; Y: truth value
#post: return the testing error
testingError <- function(w, Xtest, Ytest, w0) {
  # return (trainingError(w, X, Y, w0))
  return (sum((Ytest - Xtest%*%w - w0)^2)/dim(Ytest)[1])
}

#pre: input the optimized w^
#post: return number of non-zero coefficient
numNonZero <- function(w) {
  return (sum(w != 0))
}


training <- read.table('musicdata.txt')
testdata <- read.table("musictestdata.txt")


train <- as.matrix(training)
X <- as.matrix(train[,2:91])  #as.matrix(
Y <- as.matrix(train[,1])
test <- as.matrix(testdata)
# test <- test[2:, 2:]
Xtest <- as.matrix(test[,2:91])
Ytest <- as.matrix(test[,1])
# myMain(X, Y)
# test(X, Y)
# test begin


# testwritein <- c(1, 2, 3)


# myTest(X, Y, Xtest, Ytest)
# test end

# ===============
lamada <- 100000
allTerms <- {}
nonZeros <- {}
testingErrors <- {}
trainingErrors <- {}
while(lamada <= 2600000) {    #  350000
  cat("current lamada:",lamada, "\n")
  w <- wBeforeOptimize(X, Y, lamada)
  wOptimized <- optimize(w, X, Y, lamada)
  # wOCopy <- wOptimized
  # w0Copy2 <- wOptimized
  # w0Copy3 <- wOptimized
  w0 <- sum(Y - X%*%wOptimized)/dim(Y)[1]
  # cat("this is w0:",w0, "\n")
  # cat("this is w^:",wOptimized, "\n")
  
  # get the terms 14 ~ 19
  cat("get the terms 14 ~ 19 \n")
  terms <- c(wOptimized[14],wOptimized[15],wOptimized[16],wOptimized[17],wOptimized[18],wOptimized[19])
  # cat(terms, "\n")
  allTerms <- cbind(allTerms, terms)
  
  
  
  cat("number of non-zeros: \n")
  # cat(numNonZero(w0Copy2), "\n")
  # cat(numNonZero(wOptimized), "\n")
  nonZeros <- cbind(nonZeros, numNonZero(wOptimized))  #?
  
  cat("testing error: \n")
  testingErrors <- cbind(testingErrors, testingError(wOptimized, Xtest, Ytest, w0))
  # cat(testingError(wOptimized, Xtest, Ytest, w0), "\n") # cat(a[i], "\n")
  cat("training error: \n")
  # cat(trainingError(wOptimized, X, Y, w0), "\n") # cat(a[i], "\n")
  trainingErrors <- cbind(trainingErrors, trainingError(wOptimized, X, Y, w0))
  lamada <- lamada + 25000
}

# sink("output.txt", append = FALSE, split = TRUE)



vec14 <- allTerms[1,]
vec15 <- allTerms[2,]
vec16 <- allTerms[3,]
vec17 <- allTerms[4,]
vec18 <- allTerms[5,]
vec19 <- allTerms[6,]
lamadas <- seq(by=25000,to=2600000,from=100000)
lamadas <- as.matrix(lamadas)
loglamadas <- log(lamadas, 2)
trainout <- t(trainingErrors)
testout <- t(testingErrors)
nonzeroout <- t(nonZeros)
mac <- {}
mac <- cbind(mac, loglamadas)
mac <- cbind(mac, lamadas)
mac <- cbind(mac, trainout)
mac <- cbind(mac, testout)
mac <- cbind(mac, nonzeroout)
mac <- cbind(mac, vec14)
mac <- cbind(mac, vec15)
mac <- cbind(mac, vec16)
mac <- cbind(mac, vec17)
mac <- cbind(mac, vec18)
mac <- cbind(mac, vec19)
print(mac)
head <- c("loglamadas", "lamadas", "trainout", "testout", "nonzeroout", "vec14", "vec15", "vec16", "vec17", "vec18", "vec19")
head <- as.matrix(head)
