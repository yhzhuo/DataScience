# Author: ZHUO, Yaohua
# Warning: This implementation does not consider bit overflow issue

# dat <- c(9, 10, 11, 20, 21, 22, 45, 50, 55, 57) # this is the original data set
dat <- c(35, 42, 9, 38, 27, 31, 11, 40, 32)  # this is the final data set
z <- matrix(1,nrow=10, ncol=3, byrow=T)
t <- 1/3 # the probability of each center
std <- matrix(sd(dat), nrow=1, ncol=3, byrow=T) # the standard deviation of each center
thres <- 0.001 # the threshold


# input number of center k
# randomly pick k centers within the data return them as matrix
# each column of matrix is the center
getKCenters <- function(k) {
  ret <- dat[sample(1:length(dat),k)]
  ret <- t(as.matrix(ret))
  return (ret)
  #return (t(as.matrix(c(15.5, 51.75, 57.00))))
}

getNearestData <- function(center) {
  minDis <- abs(center-dat[1])
  ret <- dat[1]
  for(i in dat) {
    curDis <- abs(center-i)
    if(curDis < minDis) {
      minDis <- curDis
      ret <- i
    }
  }
  return (ret)
}

#P(D)
getLikelihood <- function(i, centers) {
  curLogLihood <- 0
  for(j in 1:dim(centers)[2]) {
    curLogLihood <- curLogLihood+dnorm(dat[i], centers[1, j], std[1, j])*t
  }
  return (curLogLihood)
}



# getKCenters(3)
# centerWeight is t
getTotalLogLihood <- function(centers) {
  totalLogLihood <- 0
  for(i in 1:length(dat)) {
    curLogLihood <- 0
    
    totalLogLihood <- totalLogLihood+log(getLikelihood(i, centers))
  }
  return (totalLogLihood)
}



mainFunc <- function() {
  centers <- getKCenters(3)#randomly selected approperate 3 start centers
  prevCenters <- centers - 1  # may have problems here
  cat("mu_1\tmu_2\tmu_3\tstd_1\tstd_2\tstd_3\tLogLihood\n")
  while(abs(max(centers-prevCenters)) > thres) {
    # E-step begin
    for(i in 1:length(dat)) {
      z[i, 1:3] <- 0
      maxProb <- 0
      belongToIndex <- 1 # represent which center current data point belongs to
      # the probability of first model observe data, using the equation in slide
      #oneProb <- t*dnorm(dat[i], center[1,1], std)#t*exp(-(dat[i]-center[1])^2/(2*std^2))/(std*sqrt(2*pi))
      # the probability of second model observe data, using the equation in slide
      #twoProb <- t*dnorm(dat[i], center[1,2], std)
      # the probability of third model observe data, using the equation in slide
      #threeProb <- t*dnorm(dat[i], center[1,3], std)
      for(j in 1:dim(centers)[2]) {
        # the probability of current model observe data, using the equation in slide
        curProb <- t*dnorm(dat[i], centers[1,j], std[1, j])
        curFinalProb <- curProb/getLikelihood(i, centers)
        if(curFinalProb > maxProb) {
          maxProb <- curFinalProb
          belongToIndex <- j
        }
      }
      z[i,belongToIndex] <- 1
    }
    # E-step end
    prevCenters <- centers
    # M-step begin
    #cat("######M of one EM begin######\n")
    # <maximize center>
    for(j in 1:dim(centers)[2]) {
      numerator <- 0 # fenzi
      denominator <- 0 # fenmu
      for(i in 1:length(dat)) {
        numerator <- numerator + z[i,j]*dat[i]
        denominator <- denominator + z[i,j]
      }
      #cat("======current center: ",j,"======\n")
      #cat("centers previous: \n")
      #cat(centers,"\n")
      #cat("numerator: ",numerator,"\n")
      #cat("denominator: ", denominator,"\n")
      if(denominator != 0) {  # to ensure on division error
        centers[1, j] <- numerator/denominator
      } else {
        # goes to the closest
        centers[1, j] <- getNearestData(centers[1, j])
      }
      #cat("centers now: \n")
      #cat(centers,"\n")
    }
    # </maximize center>
    # <maximize standard deviation>
    for(j in 1:dim(centers)[2]) {
      dataCount <- 0
      sqSum <- 0
      for(i in 1:length(dat)) {
        if(z[i,j] != 0) {
          dataCount <- dataCount+1
          sqSum <- sqSum + (centers[1, j]-dat[i])^2
        }
      }
      
      std[1,j] <- sqrt(sqSum/dataCount)
    }
    # cat("<===>")
    # cat("std before\n")
    # cat(std,"\n")
    if(min(std) == 0) {
      secondSmall <- max(std)
      zeroI <- 1
      for(j in dim(std)[2]) {
        if(std[1,j] < secondSmall && std[1,j] != 0) {
          secondSmall <- std[1,j]
        }
        if(std[1,j] == 0) {
          zeroI <- j
        }
      }
      # cat("zeroI: ",zeroI,"\n")
      # cat("secondSmall: ",secondSmall,"\n")
      # std[1, zeroI] <- min(c(1,secondSmall))
      std[1, zeroI] <- secondSmall
    }
    # cat("std after\n")
    # cat("zeroI: ",zeroI,"\n")
    # cat(std,"\n")
    # cat("</===>")
    # </maximize standard deviation>
    #cat("######M of one EM end######\n")
    # M-step end
    cat(centers[1, 1],"\t",centers[1, 2],"\t",centers[1, 3],"\t",std[1,1],"\t",std[1,2],"\t", std[1,3],"\t",getTotalLogLihood(centers),"\n")
  }
  #output the centers represents different categories
  cat("\n i\tx_i\tP(cls 1 |x_i)\tP(CLS 2 | X_i)\tP(cls 3 | x_i)\n")
  for(i in 1:length(dat)) {
    pD <- getLikelihood(i, centers)
    cat("[",i,",]\t")
    cat(dat[i],"\t")
    for(j in 1:dim(centers)[2]) {
      pAD <- t*dnorm(dat[i], centers[1,j], 1)/pD
      cat(pAD,"\t")
    }
    cat("\n")
  }
}
mainFunc()
