HERONPY_VERSION             := $(shell python -c "import yaml; print yaml.load(open('config.yaml', 'r'))['params']['versions']['heronpy']")
FIREBASE_PROJECT_PRODUCTION := heron-website
FIREBASE_PROJECT_STAGING    := heron-website-staging
FIREBASE_DEPLOY             := node_modules/.bin/firebase deploy --only hosting --token $(FIREBASE_TOKEN)

setup:
	scripts/setup.sh

clean:
	rm -rf public/* static/api static/style.css static/style*.css

serve:
	@hugo serve --buildDrafts

develop-static-assets:
	@npm run develop

build-static-assets:
	@npm run build

pages:
	@hugo

linkchecker:
	@scripts/linkchecker.sh

javadocs:
	@scripts/javadocs.sh

python-docs:
	@scripts/python-doc-gen.sh $(HERONPY_VERSION)

site:
	@make clean
	@make build-static-assets
	@make javadocs
	@make python-docs
	@make pages

publish:
	@scripts/publish.sh

travis-site:
	@make clean
	@make build-static-assets
	@scripts/javadocs.sh --travis
	@make python-docs
	@make pages

firebase-deploy: site
	$(FIREBASE_DEPLOY) --project $(FIREBASE_PROJECT_PRODUCTION)

firebase-deploy-staging: site
	$(FIREBASE_DEPLOY) --project $(FIREBASE_PROJECT_STAGING)