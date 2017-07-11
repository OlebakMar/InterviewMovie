# InterviewMovie
Please install mongodb on your machine to run the project.

Once you have installed mongodb, start mongodb and create the primary key index used for the project with the following command:

db.movie.ensureIndex( { "_id.imdbId": 1, "_id.screenId": 1}, { unique: true } )

Now you are ready to run the server

