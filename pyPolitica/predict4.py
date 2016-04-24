from gensim.models import word2vec
import xml.etree.ElementTree as ET
import Tweet as tw
import re
import numpy as np
from sklearn.ensemble import RandomForestClassifier
import pandas as pd
import csv
import pickle
import twokenize
import nltk.tokenize


tokenzr = nltk.data.load('tokenizers/punkt/spanish.pickle')

def readXML(xmlFIle):
    tree = ET.parse(xmlFIle)
    root = tree.getroot()
    tweets = []

    for tweet in root.iter('tweet'):
        it = tweet.itertext()
        tweet_text = ''
        tweet_text = tweet_text.join(it)

        sentiments = tweet.find('sentiment')
        polarity = polarityTagging(sentiments.get('polarity'))

        tweet = tw.Tweet('', '', '', '', tweet_text, polarity)
        tweets.append(tweet)

    return tweets


def readXML2(xmlFIle):
    tree = ET.parse(xmlFIle)
    root = tree.getroot()

    tweets = []

    for tweet in root.iter('tweet'):
        content = tweet.find('content').text

        sentiments = tweet.find('sentiments')
        polarity = sentiments[0].find('value').text

        polarity = polarityTagging_3(polarity)
        # polarity = polarityTagging3(polarity)

        # Other info:
        tweet_id = long(tweet.find('tweetid').text)
        user = tweet.find('user').text
        date = tweet.find('date').text
        lang = tweet.find('lang').text

        if content != None:
            tweet = tw.Tweet(tweet_id, user, date, lang, content, polarity)

            tweets.append(tweet)

    return tweets


def polarityTagging(polarity):
    if (polarity.__eq__('NONE')):
        polarity = 0
    elif (polarity.__eq__('N+')):
        polarity = 1
    elif (polarity.__eq__('N')):
        polarity = 2
    elif (polarity.__eq__('NEU')):
        polarity = 3
    elif (polarity.__eq__('P')):
        polarity = 4
    elif (polarity.__eq__('P+')):
        polarity = 5

    return polarity


def polarityTagging_3(polarity):
    if (polarity.__eq__('NONE')):
        polarity = 0
    elif (polarity.__eq__('N+') or polarity.__eq__('N')):
        polarity = 2
    elif (polarity.__eq__('NEU')):
        polarity = 3
    elif (polarity.__eq__('P') or polarity.__eq__('P+')):
        polarity = 4

    return polarity


def review_to_wordlist(review, remove_stopwords=False):
    # Function to convert a document to a sequence of words,
    # optionally removing stop words.  Returns a list of words.
    # 1. Remove HTML
    review_text = re.sub(r'ht\S+', 'URL', review, flags=re.MULTILINE)
    # review_text = BeautifulSoup(review).get_text()
    # 2. Remove non-letters
    # review_text = re.sub("[^a-zA-Z]"," ", review_text)
    # 3. Convert words to lower case and split them
    # tokens = tokenzr.tokenize(review.lower())
    tokens = twokenize.tokenizeRawTweetText(review.lower())
    # words = review_text.lower().split()
    # 4. Optionally remove stop words (false by default)
    # 5. Return a list of words
    return tokens


def makeFeatureVec(words, model, num_features):
    # Function to average all of the word vectors in a given
    # paragraph
    #
    # Pre-initialize an empty numpy array (for speed)
    featureVec = np.zeros((num_features,), dtype="float32")
    #
    nwords = 0.
    #
    # Index2word is a list that contains the names of the words in
    # the model's vocabulary. Convert it to a set, for speed
    #
    # Loop over each word in the review and, if it is in the model's
    # vocaublary, add its feature vector to the total
    for word in words:
        if word in index2word_set:
            nwords = nwords + 1.
            featureVec = np.add(featureVec, model[word])
    #
    # Divide the result by the number of words to get the average
    if nwords == 0:
        print words
        return np.zeros((num_features,), dtype="float32")
    featureVec = np.divide(featureVec, nwords)
    return featureVec


def getAvgFeatureVecs(reviews, model, num_features):
    # Given a set of reviews (each one a list of words), calculate
    # the average feature vector for each one and return a 2D numpy array
    #
    # Initialize a counter
    counter = 0.
    #
    # Preallocate a 2D numpy array, for speed
    reviewFeatureVecs = np.zeros((len(reviews), num_features), dtype="float32")
    #
    # Loop through the reviews
    for review in reviews:
       # Print a status message every 1000th review
       if counter%1000. == 0.:
           print("Review %d of %d" % (counter, len(reviews)))

       # Call the function (defined above) that makes average feature vectors
       reviewFeatureVecs[counter] = makeFeatureVec(review, model, num_features)
       #
       # Increment the counter
       counter = counter + 1.
    return reviewFeatureVecs


forest = RandomForestClassifier(n_estimators=100)

ts = readXML2('/Volumes/Files/Tools/datasets/SEPLN-TASS15-master/DATA/old/politics2013-tweets-test-tagged.xml')
test = readXML('/Volumes/Files/Tools/datasets/SEPLN-TASS15-master/DATA/stompol-tweets-train-tagged.xml')

print 'Loaded Data'

model_name = "/Volumes/Files/Tools/datasets/SBW-vectors-300-min5.bin.gz"
model = word2vec.Word2Vec.load_word2vec_format(model_name, binary=True)
model.init_sims(replace=True)
index2word_set = set(model.index2word)
print 'Loaded model'

trainSentimentList = []
train_text = []
for t in ts:
    trainSentimentList.append(t.polarity)
    train_text.append(review_to_wordlist(t.content))

test_text = []
testSentimentList = []
testText = []
for t in test:
    test_text.append(review_to_wordlist(t.content))
    testText.append(t.content)
    testSentimentList.append(t.polarity)


avg_features = getAvgFeatureVecs(train_text, model, 300)
print 'Train features ok'
testVecs = getAvgFeatureVecs(test_text, model, 300)
print 'Test features ok'

print("Fitting a random forest to labeled training data...")
forest = forest.fit(avg_features, trainSentimentList)
file = open('model-part-4/rf_model.txt', 'w')
pickle.dump(forest, file)
file.close()
print 'Fit ok'

# forest = pickle.load(open('model-part-4/rf_model.txt'))

result = forest.predict(testVecs)
print 'Predict ok'
output = pd.DataFrame(data={"text": testText, "polarity": result, "testPolariry": testSentimentList})
output.to_csv("model-part-4/Word2Vec_AverageVectors.csv", index=False, quoting=csv.QUOTE_ALL, encoding='utf-8')

counter = 0
for r in range(0, len(result)):
    if result[r] == testSentimentList[r]:
        counter += 1
print(counter)
print("Accuracy: ", (counter/len(result)))

