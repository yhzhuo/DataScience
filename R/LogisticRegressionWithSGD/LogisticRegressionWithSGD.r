# Author: ZHUO, Yaohua

trainingMatrix <- read.csv("train.txt", header=FALSE)
y <- as.matrix(trainingMatrix[,1])
trainingX <- trainingMatrix[,-1]
x <- as.matrix(cbind(rep(1, dim(trainingX)[1]),trainingX))

test <- read.csv("test.txt", header=FALSE)
test_label <- read.table("test_label.txt", quote="\"")
testX <- as.matrix(test[1:54])
testY <- as.matrix(test_label[,1])

# temp <- c(5,10,12,24,42,60,63,72)
# w <- matrix(temp,ncol=2)
# w <- matrix(1:55,ncol=1,byrow=T)
# w <- w * 0 # this is the start
w <- matrix(0,1,55)

predict <- function(poly) {
  if(poly > 0) {
    return (1)
  } 
  return (0)
}

# for(i in 1:length(a)) {
graDecent <- function(w, stepSize) {
  count <- 1
  loss <- 0
  store <- {}
  for (j in 1:dim(y)[1]) {
    count <- count + 1
    poly <- w[1]+sum(w[2:55]*trainingX[j,])
    expPart <- exp(poly)/(1+exp(poly))
    yPredict <- predict(poly)
    loss <- loss + (y[j] - yPredict)^2
    if(count %% 100 == 0) {
      out <- {}
      oneLoss <- loss/count
      out <- cbind(oneLoss, out)
      out <- cbind(count, out)
      store <- rbind(store, out)
      cat(oneLoss, "\n")
    }
    for(t in 1:dim(x)[2]) {
      w[t] <- w[t] + stepSize*x[j,t]*(y[j] - expPart)
    }
  }
  return (store)
}


getWeight <- function(w, stepSize) {
  for (j in 1:dim(y)[1]) {
    poly <- w[1]+sum(w[2:55]*trainingX[j,])
    expPart <- exp(poly)/(1+exp(poly))
    for(t in 1:dim(x)[2]) {
      w[t] <- w[t] + stepSize*x[j,t]*(y[j] - expPart)
    }
  }
  return (w)
}


l2Norm <- function(w) {   # sqrt(sum(xi^2))
  return (sqrt(sum(w^2)))
}



testing <- function(w, testX, testY) {
  sse <- 0
  for(i in 1:dim(testX)[1]) {
    yPredict <- w[1] + sum(testX[i, ]*w[2:55])
    if(yPredict > 0) {
      yPredict <- 1
    } else {
      yPredict <- 0
    }
    sse <- sse+(testY[i] - yPredict)^2
  }
  return (sse)
}

out5 <- graDecent(w,0.5)
out25 <- graDecent(w,0.25)
out125 <- graDecent(w,0.125)
cat("w5 info")
w5 <- getWeight(w, 0.5)
print (testing(w5, testX, testY))
print (l2Norm(w5))
cat("w25 info")
w25 <- getWeight(w, 0.25)
print (testing(w25, testX, testY))
print (l2Norm(w25))
cat("w125 info")
w125 <- getWeight(w, 0.125)
print (testing(w125, testX, testY))
print (l2Norm(w125))
