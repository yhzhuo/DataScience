# Author: ZHUO, Yaohua
data <- as.matrix(read.table("Z:/CSE446/hw4/digitdata.txt", header=T, quote="\"")) # as.matrix(read.table("digitdata.txt", header=FALSE))
trueLabels <- as.matrix(read.table("Z:/CSE446/hw4/digitlabels.txt", header=T, quote="\"")) # as.matrix(read.table("digitlabels.txt", header=FALSE))  # from my cimputer: digitlabels <- read.table("C:/CSE/2013 SPR CSE 446/hw4/digitlabels.txt", header=T, quote="\"")


# assume data nd trueLabels are fine normal matrix, they are global and immutable

# input number of center k
# randomly pick k centers within the data return them as matrix
# each row of matrix is the center
getKCenters <- function(k) {
  ret <- data[sample(1:nrow(data),k),]
  if(k == 1) {
    ret <- t(as.matrix(ret))
  }
  return (ret)
}

# input a point x 
# input a matrix of "centers" where each row is a center of one cluster
# returns which row (center) the point x is closest to
# question: is it only the row not row number? what should the type of the row be?
nearest <- function(x, centers) {
  nIndex <- 1
  # centers <- rbind(x, centers)
  minDist = getDist(x, centers[1,])
  for(i in 1:dim(centers)[1]) {
    temp <- getDist(x, centers[i,])
    if(temp < minDist) {
      minDist <- temp
      nIndex <- i
    }
  }
  # dist()
  return (nIndex)
}
  # centers <- centers[-1,]
  # return # (centers[nIndex,])

# input two data points with same dimension
# return the distance between them
getDist <- function(x, y) {
  x1 <- as.vector(x)
  y1 <- as.vector(y)
  return (sum((x1-y1)^2))
}

# input data, in type data frame
# input matrix "centers" where each row is a center of one cluster
# ? return the row number of nearest cluster in center matrix for each data point
# note: you should implement function nearest by yourself
assignCenters <- function(data, centers){
  labels <- apply(data,1, FUN = nearest, centers)
  return(labels)
}


# input data, in type data frame
# input labels as a vector
# ?note: the labels should be predicted label
# return the new center of the k means
# ?this function required the old center
newCenters <- function(data, labels){
  centers <- aggregate(data, list(label = labels),mean)
  centers <- as.matrix(centers[,-1])
  return(centers)
}

# input the trueLabels, guessLabels as two n*1 matrix, k
# retrun the error matrix
getErrorRate <- function(trueLabels, guessLabels, k) {
  result <- matrix(0, nrow=k, ncol=4)
  # for result
  # each row is the error condition of each center
  # each col is the number of a certain true label of a center
  for(i in 1:dim(trueLabels)[1]) {
    recordIndex <- 0
    if(trueLabels[i,1] == 1) {
      recordIndex <- 1
    } else if(trueLabels[i,1] == 3) {
      recordIndex <- 2
    } else if(trueLabels[i,1] == 5) {
      recordIndex <- 3
    } else {
      recordIndex <- 4
    }
    
    result[guessLabels[i,1],recordIndex] <- result[guessLabels[i,1],recordIndex]+1
  }
  return (result)
}

#input errorM as "error matrix"
#print the error rate of each center and the over error rate
printErrorRate <- function(errorM) {
  totalWrong <- 0
  totalCount <- 0
  for(i in 1:dim(errorM)[1]) {
    maxCount <- max(errorM[i,])
    curTotalCount <- sum(errorM[i,])
    wrongCount <- curTotalCount - maxCount
    totalWrong <- totalWrong + wrongCount
    totalCount <- totalCount + curTotalCount
    # cat("the error rate of center",i,"is: ")
    # cat(wrongCount/curTotalCount)
    # cat("\n")
  }
  cat("the overall error rate is: ")
  cat(totalWrong/totalCount)
  cat("\n")
}

totalSumOfSquare <- function(guessLabels, centers) {
  ret <- 0
  for(i in 1:dim(guessLabels)[1]) {
    centerIndex <- guessLabels[i,]
    value <- as.vector(data[i,])
    center <- as.vector(centers[centerIndex,])
    ret <- ret + sum((value-center)^2)
    
  }
  return (ret)
}


# below is the real step
for(k in 1:10) {
  # k <- 1
  cat("when k= ",k,"\n")
  centers <- getKCenters(k)
  oldLabels <- matrix(0, nrow=dim(data)[1], ncol=1)
  
  for(i in 1:20) {
    labels <- assignCenters(data, centers)
    
    
    if(sum(oldLabels - labels) == 0) {
      break
    }
    oldLabels <- labels
    centers <- newCenters(data, labels)
  }
  ##########question 2########
  cat("sum of within group sum of squares: \n")
  value <- totalSumOfSquare(as.matrix(labels), centers)
  cat(value,"\n")
  
  ##########question 3########
  errorM <- getErrorRate(trueLabels, as.matrix(labels), k)
  printErrorRate(errorM)
  
  cat("\n")
}

