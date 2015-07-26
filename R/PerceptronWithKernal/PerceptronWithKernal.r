# Author: ZHUO, Yaohua

validationMatrix <- as.matrix(read.csv("Z:/CSE446/hw3/validation.csv", header=FALSE))
validationY <- as.matrix(validationMatrix[2:dim(validationMatrix)[1],1])
validationY <- as.matrix(apply(validationY, 1, as.numeric))

validationX <- as.matrix(validationMatrix[2:dim(validationMatrix)[1], 2:dim(validationMatrix)[2]])
validationX <- t(apply(validationX, 1, as.numeric))


# testingMatrix <- as.matrix(read.csv("test.csv", header=FALSE))
testingMatrix <- as.matrix(read.csv("Z:/CSE446/hw3/test.csv", header=FALSE))
testingY <- as.matrix(testingMatrix[2:dim(testingMatrix)[1],1])
testingY <- as.matrix(apply(testingY, 1, as.numeric))

testingX <- as.matrix(testingMatrix[2:dim(testingMatrix)[1], 2:dim(testingMatrix)[2]])
testingX <- t(apply(testingX, 1, as.numeric))

question <- 3


kernal <- function(allErrorX, allErrorY, curX) {
  d <- 5
  theta <- 13
  if(question == 1) {
    # note: here, (t(allErrorX) %*% curX + 1) is the implementation of kernal
    return (allErrorY %*% (t(allErrorX) %*% curX + 1))
  } else if(question == 2) {
    return (allErrorY %*% ((t(allErrorX) %*% curX + 1)^d))
  } else if(question == 3) {
    # -(allErrorX - curX)^2
    eKernal <- matrix(nrow = 0, ncol = 1)
    # cat(dim(allErrorX)[2], "\n")
    for(i in 1:dim(allErrorX)[2]) {
      eKernal <- rbind(
        eKernal,exp(
          -sqrt(sum((as.matrix(allErrorX[,i]) - curX)^2))/(2*theta^2)
        )
      )
    }
    # cat(dim(allErrorY),"\n")
    # cat(dim(eKernal), "\n")
    return (allErrorY %*% eKernal)
  }
  return (matrix(nrow=0, ncol=0))
}

# realKernal <- function(allErrorXj, curX)


prob441 <- function(X, Y) {
  error <- 0
  count <- 0
  
  #Since the error terms are part of w, we store them in column, but not row
  allErrorX <- matrix(nrow=dim(X)[2], ncol=0)
  #Since the X parts of the error terms are already in columns, the Y part is in row
  allErrorY <- matrix(nrow=1, ncol=0)
  # ?: now add X1 and Y1 here as the base prediction. Is it right?
  allErrorX <- cbind(allErrorX, as.matrix(X[1,]))  # may have problems here
  allErrorY <- cbind(allErrorY, as.matrix(Y[1,]))
  for(t in 2:dim(X)[1]) {
    curX <- as.matrix(X[t,])
    if(kernal(allErrorX, allErrorY, curX)[1,1] * Y[t,] < 0) {
      allErrorX <- cbind(allErrorX, as.matrix(X[t,]))  # may have problems here
      allErrorY <- cbind(allErrorY, as.matrix(Y[t,]))
      error <- error + 1
    }
    count <- count + 1
    if(count %% 100 == 0 && count != 0) {
      cat("average loss for every 100 steps: ",error/count, "\n")
    }
  }
  if (question == 2) {
    cat("L(1000): ",error/count, "\n")
  }
}

# prob441(validationX,validationY)
prob441(testingX,testingY)
