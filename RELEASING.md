# Release Information

The project is built and released in two parts:

1. The core product and Maven plugin are released using normal Maven release support
2. The Eclipse feature is built with the Tycho Maven plugins, but doesn't use the regular Maven release support

## Pre-Requisites

1. Get authorization to Sonatype and make sure your GPG key is setup and registered per instructions here:
   https://github.com/mybatis/committers-stuff/wiki/Release-Process
2. Make sure your SSH key is setup at GitHub
3. Create a Maven Central Publishing token if you don't have one, or it is expired:
   - Logon to https://central.sonatype.com/
   - Go to Profile, View User Tokens
   - Generate a new token if you need to and add it to the maven settings file
4. Make sure your Maven `settings.xml` file is correct. At a minimum, it should contain the following:
    ```xml
    <settings>
      <servers>
        <server>
          <id>gh-pages-scm</id>
          <configuration>
            <scmVersionType>branch</scmVersionType>
            <scmVersion>gh-pages</scmVersion>
          </configuration>
        </server>
        <server>
          <id>central</id>
          <username>[tokenized user name]</username>
          <password>[tokenized password]</password>
        </server>
      </servers>
    </settings>
    ```

## Preparation

1. Make sure the version numbers are updated in the runningWithMaven.xhtml page
2. Make sure the GitHub issues and pull requests are associated with the GitHub milestone for this release.

## Release Process for Core Product and Maven Plugin

1. If you are on a Unix or Mac, then setup GPG to use the terminal when asking for your password:
   ```shell
   export GPG_TTY=$(tty)
   ```
2. Clone the main repo (with ssh), checkout the master branch, build the core product:
   ```shell
   cd core
   ./mvnw release:prepare
   ./mvnw release:perform
   ```
6. Logon to https://central.sonatype.com/
7. Go to Profile->View Deployments
8. Verify everything looks OK
9. Publish the deployment

## Release Process for the Eclipse Feature

1. If you are on a Unix or Mac, then setup GPG to use the terminal when asking for your password:
   ```shell
   export GPG_TTY=$(tty)
   ```
2. Clone the update site repo and make a branch. e.g.:
   ```shell
   git clone git@github.com:jeffgbutler/mybatis-generator-update-site.git
   cd mybatis-generator-update-site
   git switch -c mybatis-generator-2.0.0
   ```
3. Clone the main repo and checkout the release tag.  e.g.:
   ```shell
   git clone https://github.com/mybatis/generator.git
   cd generator
   git checkout mybatis-generator-2.0.0
   ```
4. Build the feature:
   ```shell
   cd eclipse
   ./mvnw clean verify -Prelease-composite
   ```
5. Copy the newly generated site into your clone of the update site repo. e.g.:
   ```shell
   cd org.mybatis.generator.eclipse.site/target/p2-composite-repo
   cp -R . ~/git/GitHub/jeffgbutler/mybatis-generator-update-site
   ```
6. Commit, push, make a PR, etc.
7. Logon to the eclipse marketplace and add the new release information
8. Add the zipped site file from `org.mybatis.generator.eclipse.site/target/p2-composite-repo/zipped` to the GitHub release page

## Final Details

1. If the DTD has changed, then update it at https://github.com/mybatis/mybatis.github.io
2. Make a Github Release that contains the Bundle and the Zipped Eclipse Site
3. Update the version numbers in all the Eclipse projects with the tycho-versions plugin.  For example:
   ```shell
   git checkout master
   cd eclipse
   ./mvnw tycho-versions:set-version -DnewVersion=1.4.3-SNAPSHOT
   git add .
   git commit -m "Update Eclipse versions for new release"
   git push
   ```

## Update the Core Site

The site will publish automatically as part of the release process. But you can do it independently too.

The following command will do a dry run of the site publishing process - you can use it to see what will be published:

```shell
./mvnw clean site scm-publish:publish-scm -Dscmpublish.dryRun=true
```

This command will publish the site:

```shell
./mvnw clean site scm-publish:publish-scm
```

If you run into issues with the publishing process, then resetting the working directory can help:

```shell
sudo rm -r ~/maven-sites
```

## Troubleshooting

### Tycho Caching

Tycho uses a cache to improve performance. This can cause issues if you try to release the Eclipse feature
before the cache expires - you will notice that the "compositeArtifacts.xml" file does not reflect the entire set
of releases on the update site. There are two ways to handle this:

1. Disabling Maven caching sometimes works:

   ```shell
   ./mvnw clean verify -Prelease-composite -U
   ```

2. If this fails, then manually clear the cache:

   ```shell
   rm -r ~/.m2/repository/.cache/tycho
   ```
