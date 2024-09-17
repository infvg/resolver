/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.resolver;

import com.google.common.collect.ImmutableList;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.airlift.resolver.ArtifactResolver.MAVEN_CENTRAL_URI;
import static io.airlift.resolver.ArtifactResolver.USER_LOCAL_REPO;
import static org.junit.Assert.assertTrue;

public class ArtifactResolverTest
{
    @Test
    public void testResolveArtifacts()
            throws Exception
    {
        ArtifactResolver artifactResolver = new ArtifactResolver(USER_LOCAL_REPO, MAVEN_CENTRAL_URI);
        List<Artifact> artifacts = artifactResolver.resolveArtifacts(ImmutableList.of(new DefaultArtifact("org.apache.maven:maven-core:3.0.4")));

        Assert.assertNotNull("artifacts is null", artifacts);
        for (Artifact artifact : artifacts) {
            Assert.assertNotNull("Artifact " + artifact + " is not resolved", artifact.getFile());
        }
    }

    @Test
    public void testResolvePom()
            throws DependencyResolutionException
    {
        File pomFile = new File("src/test/poms/maven-core-3.0.4.pom");
        assertTrue(pomFile.canRead());

        ArtifactResolver artifactResolver = new ArtifactResolver(USER_LOCAL_REPO, MAVEN_CENTRAL_URI);
        List<Artifact> artifacts = artifactResolver.resolvePom(pomFile);

        Assert.assertNotNull("artifacts is null", artifacts);
        for (Artifact artifact : artifacts) {
            Assert.assertNotNull("Artifact " + artifact + " is not resolved", artifact.getFile());
        }
    }

    @Test
    public void testResolveSiblingModule()
    {
        ArtifactResolver artifactResolver = new ArtifactResolver(USER_LOCAL_REPO, MAVEN_CENTRAL_URI);
        List<Artifact> artifacts = artifactResolver.resolvePom(new File("src/test/poms/multi-module-project/module2/pom.xml"));
        List<File> files = artifacts.stream()
                .map(Artifact::getFile)
                .filter(Objects::nonNull)
                .map(File::getAbsoluteFile)
                .collect(toImmutableList());

        assertTrue(files.contains(new File("src/test/poms/multi-module-project/module2/target/classes").getAbsoluteFile()));
        assertTrue(files.contains(new File("src/test/poms/multi-module-project/module1/target/classes").getAbsoluteFile()));
    }
}
