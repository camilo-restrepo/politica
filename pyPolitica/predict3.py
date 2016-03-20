from gensim.models import Word2Vec
import re
import csv
import numpy as np  # Make sure that numpy is imported
import pandas as pd
from pymongo import MongoClient
from nltk.corpus import stopwords

#-------------------- From part 2 ------------------------------

def review_to_wordlist(review, remove_stopwords=False):
    # Function to convert a document to a sequence of words,
    # optionally removing stop words.  Returns a list of words.
    # 1. Remove HTML
    review_text = re.sub(r'ht\S+', 'URL', review, flags=re.MULTILINE)
    # review_text = BeautifulSoup(review).get_text()
    # 2. Remove non-letters
    # review_text = re.sub("[^a-zA-Z]"," ", review_text)
    # 3. Convert words to lower case and split them
    words = review_text.lower().split()
    # 4. Optionally remove stop words (false by default)
    if remove_stopwords:
        stops = set(stopwords.words("spanish"))
        words = [w for w in words if not w in stops]
    # 5. Return a list of words
    return(words)

# Define a function to split a review into parsed sentences
def review_to_sentences(review, tokenizer, remove_stopwords=False):
    raw_sentences = tokenizer.tokenize(review.strip())
    sentences = []
    for raw_sentence in raw_sentences:
        if len(raw_sentence) > 0:
          sentences.append(review_to_wordlist(raw_sentence, remove_stopwords))
    return sentences

client = MongoClient("159.203.105.161", 27017)
db = client.boarddb
collection = db.trainingTweets
documents = collection.find()
negativeSet = []
neutralSet = []
positiveSet = []
polarity = "polarity"
dataset = []
for document in documents:
    tweetText = document["text"]
    cleanTweetText = re.sub(r'ht\S+', 'URL', tweetText, flags=re.MULTILINE)
    dataset.append({ "polarity" : document[polarity], "text": cleanTweetText })

#---------------------------------------------------------------

def makeFeatureVec(words, model, num_features):
    # Function to average all of the word vectors in a given
    # paragraph
    #
    # Pre-initialize an empty numpy array (for speed)
    featureVec = np.zeros((num_features,),dtype="float32")
    #
    nwords = 0.
    # 
    # Index2word is a list that contains the names of the words in 
    # the model's vocabulary. Convert it to a set, for speed 
    index2word_set = set(model.index2word)
    #
    # Loop over each word in the review and, if it is in the model's
    # vocaublary, add its feature vector to the total
    for word in words:
        if word in index2word_set: 
            nwords = nwords + 1.
            featureVec = np.add(featureVec,model[word])
    # 
    # Divide the result by the number of words to get the average
    featureVec = np.divide(featureVec,nwords)
    return featureVec

def getAvgFeatureVecs(reviews, model, num_features):
    # Given a set of reviews (each one a list of words), calculate 
    # the average feature vector for each one and return a 2D numpy array 
    # 
    # Initialize a counter
    counter = 0.
    # 
    # Preallocate a 2D numpy array, for speed
    reviewFeatureVecs = np.zeros((len(reviews),num_features),dtype="float32")
    # 
    # Loop through the reviews
    for review in reviews:
       #
       # Print a status message every 1000th review
       if counter%1000. == 0.:
           print("Review %d of %d" % (counter, len(reviews)))
       # 
       # Call the function (defined above) that makes average feature vectors
       reviewFeatureVecs[counter] = makeFeatureVec(review, model, num_features)
       #
       # Increment the counter
       counter = counter + 1.
    return reviewFeatureVecs

# ****************************************************************
# Calculate average feature vectors for training and testing sets,
# using the functions we defined above. Notice that we now use stop word
# removal.

model = Word2Vec.load("model-part-2/word2VecModel")
print(model.syn0.shape)
num_features = 300

train = dataset[0:2400]
test = dataset[2400:]

clean_train_reviews = []
trainSentimentList = []
for review in train:
    clean_train_reviews.append(review_to_wordlist(review["text"], remove_stopwords=True))
    trainSentimentList.append(review["polarity"])

trainDataVecs = getAvgFeatureVecs(clean_train_reviews, model, num_features)

print("Creating average feature vecs for test reviews")
clean_test_reviews = []
testText = []
testSentimentList = []
for review in test:
    text = review["text"]
    clean_test_reviews.append(review_to_wordlist(text, remove_stopwords=True))
    testText.append(text)
    testSentimentList.append(review["polarity"])

testDataVecs = getAvgFeatureVecs(clean_test_reviews, model, num_features)

#----------------------------------------------------------

# Fit a random forest to the training data, using 100 trees
from sklearn.ensemble import RandomForestClassifier
forest = RandomForestClassifier( n_estimators = 100 )

print("Fitting a random forest to labeled training data...")
forest = forest.fit(trainDataVecs, trainSentimentList)

# Test & extract results 
result = forest.predict(testDataVecs)

# Write the test results 
output = pd.DataFrame(data={"text":testText, "polarity":result, "testPolariry": testSentimentList})
output.to_csv("model-part-3/Word2Vec_AverageVectors.csv", index=False, quoting=csv.QUOTE_ALL)

# comparar vs classified tweets.
counter = 0
for r in range(0, len(result)):
    if result[r] == testSentimentList[r]:
        counter += 1
print(counter)
print("Accuracy: ", (counter/len(result)))

#------------------------- Attempt 2 -----------------------------

from sklearn.cluster import KMeans
import time
import math

start = time.time() # Start time

# Set "k" (num_clusters) to be 1/5th of the vocabulary size, or an
# average of 5 words per cluster
word_vectors = model.syn0
num_clusters = math.floor(word_vectors.shape[0] / 5)
# num_clusters = 100 # -1, 0, 1

# Initalize a k-means object and use it to extract centroids
kmeans_clustering = KMeans( n_clusters = num_clusters )
idx = kmeans_clustering.fit_predict( word_vectors )

# Get the end time and print how long the process took
end = time.time()
elapsed = end - start
print("Time taken for K Means clustering: ", elapsed, "seconds.")

# Create a Word / Index dictionary, mapping each vocabulary word to a cluster number
word_centroid_map = dict(zip(model.index2word, idx))

# For the first 10 clusters
for cluster in range(0, 10):
    #
    # Print the cluster number  
    print("Cluster", cluster)
    #
    # Find all of the words for that cluster number, and print them out
    words = []
    for i in range(0,len(word_centroid_map.values())):
        if(list(word_centroid_map.values())[i] == cluster):
            words.append(list(word_centroid_map.keys())[i])
    print(words)


def create_bag_of_centroids( wordlist, word_centroid_map ):
    #
    # The number of clusters is equal to the highest cluster index
    # in the word / centroid map
    num_centroids = max( word_centroid_map.values() ) + 1
    #
    # Pre-allocate the bag of centroids vector (for speed)
    bag_of_centroids = np.zeros( num_centroids, dtype="float32" )
    #
    # Loop over the words in the review. If the word is in the vocabulary,
    # find which cluster it belongs to, and increment that cluster count 
    # by one
    for word in wordlist:
        if word in word_centroid_map:
            index = word_centroid_map[word]
            bag_of_centroids[index] += 1
    #
    # Return the "bag of centroids"
    return bag_of_centroids


# Pre-allocate an array for the training set bags of centroids (for speed)
train_centroids = np.zeros((len(train), num_clusters), dtype="float32")

# Transform the training set reviews into bags of centroids
counter = 0
for review in clean_train_reviews:
    train_centroids[counter] = create_bag_of_centroids( review, word_centroid_map )
    counter += 1

# Repeat for test reviews 
test_centroids = np.zeros((len(test), num_clusters), dtype="float32")

counter = 0
for review in clean_test_reviews:
    test_centroids[counter] = create_bag_of_centroids( review, word_centroid_map )
    counter += 1

# Fit a random forest and extract predictions 
forest = RandomForestClassifier(n_estimators = 100)

# Fitting the forest may take a few minutes
print("Fitting a random forest to labeled training data...")
forest = forest.fit(train_centroids, trainSentimentList)
result = forest.predict(test_centroids)

# Write the test results 
output = pd.DataFrame(data={"text":testText, "polarity": result})
output.to_csv("model-part-3/BagOfCentroids.csv", index=False, quoting=csv.QUOTE_ALL)

counter = 0
for r in range(0, len(result)):
    if result[r] == testSentimentList[r]:
        counter += 1
print(counter)
print("Accuracy: ", (counter/len(result)))
