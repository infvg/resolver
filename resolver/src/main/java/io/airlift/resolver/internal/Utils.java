package io.airlift.resolver.internal;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicyRequest;
import org.eclipse.aether.transfer.RepositoryOfflineException;

public class Utils {
    public static void checkOffline(RepositorySystemSession session, OfflineController offlineController, RemoteRepository repository) throws RepositoryOfflineException {
        if (session.isOffline()) {
            offlineController.checkOffline(session, repository);
        }

    }
    public static int getPolicy(RepositorySystemSession session, Artifact artifact, RemoteRepository repository) {
        ResolutionErrorPolicy rep = session.getResolutionErrorPolicy();
        return rep == null ? 0 : rep.getArtifactPolicy(session, new ResolutionErrorPolicyRequest(artifact, repository));
    }

    public static int getPolicy(RepositorySystemSession session, Metadata metadata, RemoteRepository repository) {
        ResolutionErrorPolicy rep = session.getResolutionErrorPolicy();
        return rep == null ? 0 : rep.getMetadataPolicy(session, new ResolutionErrorPolicyRequest(metadata, repository));
    }
}
