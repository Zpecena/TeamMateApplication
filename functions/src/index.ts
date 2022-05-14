import * as functions from 'firebase-functions'
import * as admin  from 'firebase-admin'
import * as algoliasearch from 'algoliasearch'

admin.initializeApp();
const env = functions.config();

//Initialize Algolia Client
const client = algoliasearch(env.algolia.appid, env.algolia.apikey);
const index = client.initIndex('dev_NAME');

exports.indexVenue = functions.firestore
	.document('venues/{venueId}')
	.onCreate((snap, context) => {
		const data = snap.data();
		const FirebaseObjectId = snap.id;

		// Add data to Algolia index
		return index.addObject({
			FirebaseObjectId,
			...data
		});
	});

exports.unindexVenue = functions.firestore
  .document('venues/{venueId}')
  .onDelete((snap, context) => {
    const FirebaseObjectId = snap.id;

    // Delete an ID from the index
    return index.deleteObject(FirebaseObjectId);
}); 