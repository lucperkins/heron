const gulp     = require("gulp"),
      sass     = require("gulp-sass"),
      hash     = require("gulp-hash"),
      prefixer = require("gulp-autoprefixer"),
      uglify   = require("gulp-uglify"),
      del      = require("del");

const SRCS = {
  sass: 'assets/sass/style.scss',
  sassWatch: 'assets/sass/**/*.scss',
  js: 'assets/js/app.js',
  hash: 'hash.json'
}

const DIST = {
  css: 'static/css',
  js: 'static/js'
}

const sassConfig = {
  outputStyle: 'compressed'
}

const prefixerConfig = {
  browsers: ['last 2 versions'],
  cascade: false
}

gulp.task('sass', (done) => {
  del(['css/style-*.css']);

  gulp.src(SRCS.sass)
    .pipe(sass(sassConfig).on('error', sass.logError))
    .pipe(hash())
    .pipe(prefixer(prefixerConfig))
    .pipe(gulp.dest(DIST.css))
    .pipe(hash.manifest(DIST.hash))
    .pipe(gulp.dest('data/css'));
  done();
});

gulp.task('sass:watch', () => {
  gulp.watch(SRCS.sassWatch, gulp.series('sass'));
});

gulp.task('js', (done) => {
  del(['/js/app-*.js']);

  gulp.src(SRCS.js)
    .pipe(hash())
    .pipe(gulp.dest(DIST.js))
    .pipe(hash.manifest(DIST.hash))
    .pipe(gulp.dest('data/js'));
  done();
});

gulp.task('js:watch', () => {
  gulp.watch(SRCS.js, gulp.series('js'));
});

gulp.task('build', gulp.series('sass', 'js'));

gulp.task('dev', gulp.series('build', gulp.parallel('sass:watch', 'js:watch')));
